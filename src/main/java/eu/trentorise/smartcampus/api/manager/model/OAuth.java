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
package eu.trentorise.smartcampus.api.manager.model;




/**
 * Model for oauth policy:
 * name : name of policy must be unique,
 * token : access token, we expect to be in request header not in model,
 * operation : verify token, validate token,
 * validate endpoint : endpoint to validate access token (scope and expiry).
 * 
 * @author Giulia Canobbio
 *
 */
public class OAuth extends Policy{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private String op;
	private String endpoint;
	
	/**
	 * Operation value can be: 
	 * Verify Token,
	 * Validate Token.
	 * 
	 * @return operation : String
	 */
	public String getOp() {
		return op;
	}
	/**
	 * Operation value can be: 
	 * Verify Token,
	 * Validate Token.
	 * 
	 * @param op : String
	 */
	public void setOp(String op) {
		this.op = op;
	}
	/**
	 * 
	 * @return oauth endpoint : String
	 */
	public String getEndpoint() {
		return endpoint;
	}
	/**
	 * 
	 * @param endpoint : String
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
}
