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
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Policy Spike Arrest
 * id, app id, api id, resource id and time.
 * 
 * @author Giulia Canobbio
 *
 */
@Document
public class LastTime implements Serializable{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	@Field
	private String appId;
	@Field
	private String apiId;
	@Field
	private String resourceId;
	@Field
	private Date time;
	
	/**
	 * 
	 * @return id : String
	 */
	public String getId() {
		return id;
	}
	/**
	 * 
	 * @param id : String
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 
	 * @return app id : String
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * 
	 * @param appId : String
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
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
	 * @return time : {@link Date}
	 */
	public Date getTime() {
		return time;
	}
	/**
	 * 
	 * @param time : {@link Date}
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	
}