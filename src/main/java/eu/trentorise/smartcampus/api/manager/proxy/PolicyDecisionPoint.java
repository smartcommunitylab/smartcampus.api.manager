/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.api.manager.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.googleAnalytics.GoogleAnalyticsPostCollect;
import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.OAuth;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SAML;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.VerifyAppKey;
import eu.trentorise.smartcampus.api.manager.model.util.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManagerProxy;
import eu.trentorise.smartcampus.api.manager.persistence.UserManager;

/**
 * Class that retrieves policies of api and its resources.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
public class PolicyDecisionPoint {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PolicyDecisionPoint.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager manager;
	/**
	 * Instance of {@link PersistenceManagerProxy}.
	 */
	@Autowired
	private PersistenceManagerProxy proxyManager;
	/**
	 * Instance of {@link GoogleAnalyticsPostCollect}.
	 */
	private GoogleAnalyticsPostCollect gatemplate;
	/**
	 * Instance of {@link UserManager}
	 */
	@Autowired
	private UserManager userManager;
	/**
	 * Api owner trackingID
	 */
	private String trackingID;
	
	private String apiName, rName;
	
	/**
	 * Retrieves api policies and resource policies.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param method : String, request method (GET, POST, PUT or DELETE)
	 * @return list of instance {@link Policy}
	 */
	private List<Policy> policiesList(String apiId, String resourceId, String method) {
		// logger.info("policiesList() - ApiId: {}", apiId);

		List<Policy> pToApply = new ArrayList<Policy>();
		boolean qfound = false, spfound = false, ipfound = false, vappfound = false, oauthfound = false, 
				samlfound = false;
		
		// api policies
		try {
			Api api = manager.getApiById(apiId);
			
			if(api==null){
				throw new SecurityException("You are not allowed to access this api because it does not exist");
			}
			
			//retrieve api name
			apiName = api.getName();
			
			//retrieve tracking id of user
			String usernameOwner = api.getOwnerId();
			if(userManager.isTrackingIDSave(usernameOwner)){
				trackingID = userManager.getTrackingID(usernameOwner);
			}

			// resource policies
			if (resourceId != null) {

				Resource r = manager.getResourceApiByResourceId(apiId,
						resourceId);
				
				if(r==null){
					throw new SecurityException("You are not allowed to access this resource because it does not exist");
				}
				
				//check verb TODO
				if(!r.getVerb().equalsIgnoreCase(method)){
					throw new SecurityException("Method request is wrong. Try again with the correct resource verb.");
				}
				
				//resource name
				rName = r.getName();
				
				// retrieve policy resource
				List<Policy> rplist = r.getPolicy();
				if (rplist != null && rplist.size() > 0) {
					pToApply.addAll(rplist);
				}
				
				//check which type of policy are added to list for resource
				if (listContainsClass(pToApply,"Quota")) {
					qfound=true;
				}
				if (listContainsClass(pToApply,"Spike Arrest")) {
					spfound=true;
				}
				if (listContainsClass(pToApply,"IP Access Control")) {
					ipfound=true;
				}
				if (listContainsClass(pToApply,"Verify App Key")) {
					vappfound=true;
				}
				if(listContainsClass(pToApply, "OAuth")){
					oauthfound = true;
				}
				if(listContainsClass(pToApply, "SAML")){
					samlfound = true;
				}

			}
			// api policies

			List<Policy> policies = api.getPolicy();
			if (policies != null && policies.size() > 0) {
				for (int i = 0; i < policies.size(); i++) {
					if (policies.get(i) instanceof Quota && !qfound) {
						pToApply.add(policies.get(i));
					} 
					if (policies.get(i) instanceof SpikeArrest && !spfound) {
						pToApply.add(policies.get(i));
					}
					if (policies.get(i) instanceof IPAccessControl && !ipfound) {
						pToApply.add(policies.get(i));
					}
					if (policies.get(i) instanceof VerifyAppKey && !vappfound) {
						pToApply.add(policies.get(i));
					}
					if (policies.get(i) instanceof OAuth && !oauthfound) {
						pToApply.add(policies.get(i));
					}
					if (policies.get(i) instanceof SAML && !samlfound) {
						pToApply.add(policies.get(i));
					}
				}

			}
	
		} catch (NullPointerException n) {
			logger.info("No policies for this api {}", apiId);
			//throw new IllegalArgumentException("No policies for this api "+ apiId);
		}

		return pToApply;
	}
	
	/**
	 * Check if a type of policy is in the list.
	 * Type is the type of policy to search and its possible value are:
	 * 1- Quota
	 * 2- Spike Arrest
	 * 3- IP Access Control
	 * 4- Verify App Key
	 * 5- OAuth
	 * 6- SAML
	 * 
	 * @param list : list of {@link Policy}
	 * @param type : String
	 * @return boolean: true if a type of policy is found, otherwise false
	 */
	private boolean listContainsClass(List<Policy> list, String type){
		for(int i=0;i<list.size();i++){
			//Quota
			if(type.equalsIgnoreCase("Quota")){
				if(list.get(i) instanceof Quota){
					return true;
				}
			}
			//Spike Arrest
			if(type.equalsIgnoreCase("Spike Arrest")){
				if(list.get(i) instanceof SpikeArrest){
					return true;
				}
			}
			//IP Access Control
			if(type.equalsIgnoreCase("IP Access Control")){
				if(list.get(i) instanceof IPAccessControl){
					return true;
				}
			}
			//Verify App Key
			if(type.equalsIgnoreCase("Verify App Key")){
				if(list.get(i) instanceof VerifyAppKey){
					return true;
				}
			}
			//OAuth
			if(type.equalsIgnoreCase("OAuth")){
				if(list.get(i) instanceof OAuth){
					return true;
				}
			}
			//SAML
			if(type.equalsIgnoreCase("SAML")){
				if(list.get(i) instanceof SAML){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Apply policy logic to the request.
	 * 
	 * @param obj : instance of {@link RequestHandlerObject}
	 * @param method : String, request method (GET, POST, PUT or DELETE)
	 */
	public void applyPoliciesBatch(RequestHandlerObject obj, String method){
		
		String apiId = obj.getApiId();
		String resourceId = obj.getResourceId();
		String appId = obj.getAppId();
		Map<String, String> headers = obj.getHeaders();

		//TODO add request method
		List<Policy> pToApply = policiesList(apiId, resourceId, method);
		
		PolicyDatastoreBatch batch = new PolicyDatastoreBatch();
		
		//Post data event and exception to owner id
		if(trackingID!=null){
			gatemplate = new GoogleAnalyticsPostCollect(trackingID, "Api Manager", "1");
		}
		
		if (pToApply != null && pToApply.size() > 0) {
			for (int i = 0; i < pToApply.size(); i++) {
				
				if(pToApply.get(i) instanceof Quota){
					
					QuotaApply qa = new QuotaApply(apiId, resourceId, appId,(Quota)pToApply.get(i));
					qa.setPManager(proxyManager);
					qa.setManager(manager);
					batch.add(qa);
				}
				else if(pToApply.get(i) instanceof SpikeArrest){
					
					SpikeArrestApply saa = new SpikeArrestApply(apiId,resourceId,appId,
							(SpikeArrest)pToApply.get(i));
					saa.setPManager(proxyManager);
					batch.add(saa);
				}
				else if(pToApply.get(i) instanceof IPAccessControl){
					
					String appIp = headers.get("x-forwarded-for");
					//logger.info("PDP app ip: {}",appIp);
					IPAccessControlApply ipa = new IPAccessControlApply(apiId, resourceId, 
							(IPAccessControl)pToApply.get(i), appIp);
					batch.add(ipa);
				}
				else if(pToApply.get(i) instanceof VerifyAppKey){
					
					String appSecret = headers.get("appSecret");
					VerifyAppKeyApply vapp = new VerifyAppKeyApply(apiId, resourceId,appId,
							(VerifyAppKey)pToApply.get(i), appSecret);
					vapp.setManager(manager);
					batch.add(vapp);
				}
				else if(pToApply.get(i) instanceof OAuth){
					String token = headers.get("token");
					OAuthApply oauth = new OAuthApply(apiId, resourceId, appId, token,(OAuth)pToApply.get(i));
					batch.add(oauth);
				}
				else if(pToApply.get(i) instanceof SAML){
					//TODO where to retrieve samlassertion? Seems in request body
					String samlAssertion = headers.get("samlart");//for test in headers
					SAMLApply saml = new SAMLApply(apiId, resourceId, (SAML)pToApply.get(i), samlAssertion);
					batch.add(saml);
				}
			}
			//apply policies
			MongoRollback rollback = new MongoRollback();
			rollback.setPmanager(proxyManager);
			
			try{
				batch.apply();
				rollback.successfulPolicySP(apiId, resourceId, appId);
				rollback.successfulPolicyQ(apiId, resourceId, appId);
				
				//Access granted ga
				if(gatemplate!=null){
					//event of access granted
					boolean r;
					if(resourceId==null){
						r = gatemplate.eventTracking("API", "Access Granted", apiName, "1");
					}else
						r = gatemplate.eventTracking("API", "Access Granted", apiName+"/"+rName, "1");
				
					logger.info("Write event {}", r);
				}
				
				//TODO request to api
				
			}catch(SecurityException s){
				String msg = s.getMessage();
				logger.info("Cause of security exception: {}",msg);
				
				String cause = exceptionCause(msg);
				
				rollback.failurePolicy(apiId, resourceId, appId, "Quota");
				rollback.failurePolicy(apiId, resourceId, appId, "Spike Arrest");
				
				//exception
				if(resourceId==null){
					
					if (gatemplate != null) {
						//Access denied ga
						boolean r1 = gatemplate.eventTracking("API", "Access Denied", apiName, "1");
						//Exception on api (policy exception) ga
						boolean r2 = gatemplate.exceptionTracking(apiName+ " "+cause, false);
						logger.info("Write event {}", r1);
						logger.info("Write exception {}", r2);
					}
					
					throw new SecurityException("You are not allowed to access this api. "+msg);
				}
				else{
					if (gatemplate != null) {
						//Access denied ga
						boolean r1 = gatemplate.eventTracking("API", "Access Denied", apiName+"/"+rName, "1");
						//Exception on api (policy exception) ga
						boolean r2 = gatemplate.exceptionTracking(apiName+" "+cause, false);
						
						logger.info("Write event {}", r1);
						logger.info("Write exception {}", r2);
					}
					
					throw new SecurityException("You are not allowed to access this resource."
							+msg);
				}
				
			}
			
		} else {
			logger.info("Access -> GRANT");
			//throw new IllegalArgumentException("There is no policies to apply");
			
			//Access granted ga
			if(gatemplate!=null){
				//event of access granted
				boolean r;
				if(resourceId==null){
					r = gatemplate.eventTracking("API", "Access Granted", apiName, "1");
				}else
					r = gatemplate.eventTracking("API", "Access Granted", apiName+"/"+rName, "1");
				
				logger.info("Write event {}", r);
			}
			
			//TODO request to api
		}
	}
	
	/**
	 * Function that retrieves which policy threw exception.
	 * 
	 * @param msg : String
	 * @return string : Ip Access Control, OAuth, Quota, SAML, Spike Arrest or 
	 * 			Verify App Key Policy Exception, otherwise it is a Security Exception.
	 */
	private String exceptionCause (String msg){
		//Ip Access Control
		if(msg.contains("Ip Access Control")){
			return "Ip Access Control Policy Exception";
		}
		
		//OAuth
		if(msg.contains("OAuth")){
			return "OAuth Policy Exception";
		}

		//Quota
		if(msg.contains("Quota")){
			return "Quota Policy Exception";
		}

		//SAML
		if(msg.contains("SAML")){
			return "SAML Policy Exception";
		}
		
		//Spike Arrest
		if(msg.contains("Spike Arrest")){
			return "Spike Arrest Policy Exception";
		}

		//Verify App Key
		if(msg.contains("Verify App Key")){
			return "Verify App Key Policy Exception";
		}
		
		return "Security Exception";
	}
}
