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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.smartcampus.api.manager.model.ApiData;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.VerifyAppKey;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Class that apply policy Verify App Key.
 * 
 * @author Giulia Canobbio
 *
 */
public class VerifyAppKeyApply implements PolicyDatastoreApply{
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(VerifyAppKeyApply.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	private PersistenceManager manager;
	
	//global variables
	private String apiId;
	private String resourceId;
	private String appId;
	private String appSecret;
	private VerifyAppKey p;
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param p : instance of {@link VerifyAppKey}
	 */
	public VerifyAppKeyApply(String apiId, String resourceId, String appId, VerifyAppKey p, 
			String appSecret){
		this.apiId = apiId;
		this.resourceId = resourceId;
		this.appId = appId;
		this.p = p;
		this.appSecret = appSecret;
	}
	
	/**
	 * 
	 * @param pmanager : instance of {@link PersistenceManager}
	 */
	public void setManager(PersistenceManager manager) {
		this.manager = manager;
	}

	@Override
	public void apply() {
		
		decision();
		
	}
	
	/**
	 * Function that decide if an access to resource or api with can be granted or not 
	 * by applying verify app key policy.
	 * It throws a security exception if access is denied.
	 */
	private void decision(){
		
		boolean decision;
		
		// check that resource id cannot be null
		if (apiId == null && resourceId == null) {
			throw new IllegalArgumentException("Api or Resource id cannot be null.");
		}
		else{
			decision=verifyAppKeyDecision(apiId, resourceId, appId, p, appSecret);
		}
		
		if(decision)
			logger.info("Verify App Key policy --> GRANT ");
		else{
			logger.info("Verify App Key policy --> DENY ");
			throw new SecurityException("DENY - " +
					" Verify App Key policy DENIES access.");
		}
	}
	
	/**
	 * Applies policy verify app key.
	 * First it checks if parameter anonymous is true or false.
	 * If true then anonymous access is granted and return true, otherwise
	 * it checks that api id and/or resource id are in list of Api in App data.
	 * If they are found then return true, else false.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return boolean value, true if access is granted otherwise false
	 */
	private boolean verifyAppKeyDecision(String apiId, String resourceId, String appId, 
			VerifyAppKey p, String appKey){
		// check if anonymous is true or false
		boolean anonymous = p.isAnonymous();
		// if true => grant
		if(anonymous){
			return true;
		}
		else{
			// retrieve App data
			App app = manager.getAppById(appId);
			// check that apiId or resource id is in App api list
			try {
				List<ApiData> lapi = app.getApis();
				if (lapi != null && lapi.size() > 0) {
					for (int i = 0; i < lapi.size(); i++) {
						// if App api list contains api id => grant &&
						// resourceId is not checked
						if (lapi.get(i).getApiId().equalsIgnoreCase(apiId)
								&& app.getKey().equalsIgnoreCase(appKey)) {
							return true;
						}
					}
				}
			} catch (java.lang.NullPointerException n) {
				return false;
			}
		}

		return false;
	}

}
