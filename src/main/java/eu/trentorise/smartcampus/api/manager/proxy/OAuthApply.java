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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.smartcampus.api.manager.model.OAuth;

public class OAuthApply implements PolicyDatastoreApply{
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(OAuthApply.class);
	
	// global variable 
	private String apiId;
	private String resourceId;
	private String appId; // valore non utilizzato nel caso solo validatione/verifica presenza token
	private String token;// get from request header
	private OAuth p;
	
	public OAuthApply(String apiId, String resourceId, String appId, String token, OAuth p){
		this.apiId = apiId;
		this.resourceId = resourceId;
		this.appId = appId;
		this.token = token;
		this.p = p;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		decision();
	}
	
	private void decision() {
		
		// both api and resource id cannot be null
		if (apiId == null && resourceId == null) {
			throw new IllegalArgumentException(
					"Api or Resource id cannot be null.");
		} else {
			boolean decision;

			decision = OAuthDecision(p.getOp(), p.getEndpoint(), apiId,
					resourceId, appId, token);

			if (decision)
				logger.info("OAuth policy --> GRANT");
			else
				logger.info("OAuth policy --> DENY");
			throw new SecurityException("DENY - " +
					" OAuth policy DENIES access.");
		}
	}
	
	public boolean OAuthDecision(String operation, String validateEndpoint,
			String apiId, String resourceId, String appId, String token) {

		if ((token==null || token.isEmpty()) && operation.equalsIgnoreCase("validateToken")) {
			logger.info("Request a token for api {}", apiId);
			throw new IllegalArgumentException(
					"Token is required. Request a new one for api.");
		} else {

			if (operation.equals("verifyToken")) {
				logger.info("Token found");
				return true;
			} else {
				
				if(token!=null && operation.equalsIgnoreCase("validateToken") && p.getEndpoint()!=null){
					//TODO
					//endpoint
					//request valid token to endpoint
					//check that it is still valid
					//if not then check scope and expiry
					return true;
				}
				
			}
		}
		return false;

	}				

}
