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

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Model for api data in app model.
 * 
 * @author Giulia Canobbio
 *
 */
@Document
public class ApiData implements Serializable{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	@Field
	private String apiId;
	@Field
	private String apiStatus;
	
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
	 * @return api status : String
	 */
	public String getApiStatus() {
		return apiStatus;
	}
	/**
	 * 
	 * @param apiStatus : String
	 */
	public void setApiStatus(String apiStatus) {
		this.apiStatus = apiStatus;
	}
	
}
