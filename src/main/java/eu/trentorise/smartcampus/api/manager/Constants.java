/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.api.manager;

/**
 * Constants.
 * 
 * @author Giulia Canobbio.
 *
 */
public class Constants {
	
	/**
	 * Possible value of verb field in Resource.
	 */
	public enum VERB {GET, POST, PUT, DELETE};
	/**
	 * Possible value of policy category
	 */
	public enum POLICY_CATEGORY {QualityOfService, Security};
	/**
	 * possible value of api status
	 */
	public enum API_STATUS {SILVER, GOLD, PLATINUM};
	/**
	 * possible value for rule of policy IP Access Control
	 */
	public enum POLICY_IP_RULE{ALLOW,DENY};
	
}