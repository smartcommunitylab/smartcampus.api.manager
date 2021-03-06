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
package eu.trentorise.smartcampus.api.manager.model.proxy;

import java.io.Serializable;

/**
 * Model for aggregation on mongodb that retrieves 
 * total counter not taking into account app id.
 * 
 * @author Giulia Canobbio
 *
 */
public class GlobalCounter implements Serializable{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private String apiId;
	private String resourceId;
	private int total;
	
	/**
	 * 
	 * @return api id : String
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
	 * @return resource id : String
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
	 * @return total counter : int
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * 
	 * @param total : int
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
