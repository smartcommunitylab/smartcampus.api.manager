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
package eu.trentorise.smartcampus.api.manager.model.util;

/**
 * In memory object, with url, apiId, resourceId and
 * flag pattern.
 * 
 * @author Giulia Canobbio
 *
 */
public class ObjectInMemory {
	
	private String apiId;
	private String resourceId;
	private boolean pattern;
	
	/**
	 * 
	 * @return api id, String
	 */
	public String getApiId() {
		return apiId;
	}
	/**
	 * 
	 * @param apiId : String
	 */
	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
	/**
	 * 
	 * @return resource id, String
	 */
	public String getResourceId() {
		return resourceId;
	}
	/**
	 * 
	 * @param resourceId : String
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	/**
	 * 
	 * @return pattern : boolean
	 */
	public boolean isPattern() {
		return pattern;
	}
	/**
	 * 
	 * @param pattern : boolean
	 */
	public void setPattern(boolean pattern) {
		this.pattern = pattern;
	}
	

}
