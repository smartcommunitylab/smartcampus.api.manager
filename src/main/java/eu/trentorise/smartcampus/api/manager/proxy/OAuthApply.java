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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.trentorise.smartcampus.api.manager.model.OAuth;
import eu.trentorise.smartcampus.api.manager.model.util.ValidateToken;
/**
 * Apply oauth policy logic.
 * 
 * @author Giulia Canobbio
 *
 */
public class OAuthApply implements PolicyDatastoreApply{
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(OAuthApply.class);
	/**
	 * Instance of {@link RestTemplate}
	 */
	private RestTemplate rtemplate;
	
	// global variable 
	private String apiId;
	private String resourceId;
	private String appId; // not needed value, only for validation/check that token is in request
	private String token;// get from request header
	private OAuth p;
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param token : String
	 * @param p : instance of {@link OAuth}
	 */
	public OAuthApply(String apiId, String resourceId, String appId, String token, OAuth p){
		this.apiId = apiId;
		this.resourceId = resourceId;
		this.appId = appId;
		this.token = token;
		this.p = p;
	}

	@Override
	public void apply() {

		decision();
	}
	
	/**
	 * Checks if access to an api resource can be granted or not.
	 * It throws a security exception if access is denied.
	 */
	private void decision() {
		
		// both api and resource id cannot be null
		if (apiId == null && resourceId == null) {
			throw new IllegalArgumentException(
					"Api or Resource id cannot be null.");
		} else {
			boolean decision = OAuthDecision(p.getOp(), p.getEndpoint(), apiId,
					resourceId, appId, token);

			if (decision)
				logger.info("OAuth policy --> GRANT");
			else{
				logger.info("OAuth policy --> DENY");
				throw new SecurityException("DENY - OAuth policy DENIES access.");
			}
		}
	}
	
	/**
	 * Apply oauth logic.
	 * If token is null and operation is validate token, then an illegal argument exception is threw.
	 * Else if operation is verify token then access is granted, if operation is validate token and 
	 * endpoint is not null then a request to the endpoint is sent to check if token is still valid.
	 * If it is then access is granted otherwise it is denied.
	 * 
	 * @param operation : String, value can be validate token or verify token
	 * @param validateEndpoint : String
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param token : String
	 * @return boolean value: true if access is grant otherwise false
	 */
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
					
					rtemplate = new RestTemplate();
					
					try{
						//Endpoint returns a boolean value: true if token is valid else false
						ValidateToken result = rtemplate.getForObject(p.getEndpoint(),
							ValidateToken.class, new Object[]{});
						//check that active is true
						if(result.isActive()){
							return true;
						}
					}catch(HttpClientErrorException e){
						return false;
					}
				}
				
			}
		}
		return false;

	}				

}
