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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.VerifyAppKey;
import eu.trentorise.smartcampus.api.manager.model.proxy.LastTime;
import eu.trentorise.smartcampus.api.manager.model.proxy.PolicyQuota;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManagerProxy;

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
	 * Retrieves api policies and resource policies.
	 * 
	 * @return list of instance {@link Policy}
	 */
	private List<Policy> policiesList(String apiId, String resourceId) {
		// logger.info("policiesList() - ApiId: {}", apiId);

		List<Policy> pToApply = new ArrayList<Policy>();
		boolean qfound = false, spfound = false, ipfound = false, vappfound = false;
		
		// api policies
		try {
			Api api = manager.getApiById(apiId);

			// resource policies
			if (resourceId != null) {

				Resource r = manager.getResourceApiByResourceId(apiId,
						resourceId);
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
				}

			}
	
		} catch (NullPointerException n) {
			logger.info("No policies for this api {}", apiId);
			//throw new IllegalArgumentException("No policies for this api "+ apiId);
		}

		return pToApply;
	}
	
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
		}
		return false;
	}
	
	/**
	 * Apply policy logic to the request.
	 * 
	 * @param obj : instance of {@link RequestHandlerObject}
	 */
	public void applyPoliciesBatch(RequestHandlerObject obj){
		
		String apiId = obj.getApiId();
		String resourceId = obj.getResourceId();
		String appId = obj.getAppId();
		Map<String, String> headers = obj.getHeaders();

		List<Policy> pToApply = policiesList(apiId, resourceId);
		
		PolicyDatastoreBatch batch = new PolicyDatastoreBatch();
		
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
					
					String appIp = headers.get("X-FORWARDED-FOR");
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
			}
			//apply policies
			MongoRollback rollback = new MongoRollback();
			rollback.setPmanager(proxyManager);
			try{
				batch.apply();
				rollback.successfulPolicySP(apiId, resourceId, appId);
				rollback.successfulPolicyQ(apiId, resourceId, appId);
				
			}catch(SecurityException s){
				String msg = s.getMessage();
				logger.info("Cause of security exception: {}",msg);
				
				rollback.failurePolicy(apiId, resourceId, appId, "Quota");
				rollback.failurePolicy(apiId, resourceId, appId, "Spike Arrest");
				
				//exception
				if(resourceId==null)
					throw new SecurityException("You are not allowed to access this api. "
							+s.getMessage());
				else
					throw new SecurityException("You are not allowed to access this resource. "
							+s.getMessage());
			}
			
		} else {
			logger.info("Access -> GRANT");
			//throw new IllegalArgumentException("There is no policies to apply");
		}
	}
}
