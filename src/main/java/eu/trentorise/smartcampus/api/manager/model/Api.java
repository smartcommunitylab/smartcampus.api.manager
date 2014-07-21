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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Api model
 * id: String,
 * name: String,
 * base path: String,
 * resource: String[],
 * policy: String[],
 * app : String[],
 * owner id : String,
 * creationTime : String,
 * updateTime : String.
 * 
 * @author Giulia Canobbio
 *
 */
@Document
public class Api implements Serializable{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	@Field("name")
	private String name;
	@Field("basePath")
	private String basePath;
	@Field
	private String resource[];
	@Field
	private String policy[];
	@Field
	private String app[];
	@Field
	private String ownerId;
	@Field
	private String creationTime;
	@Field
	private String updateTime;
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
	 * @return name : String
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name : String
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return project base path : String
	 */
	public String getBasePath() {
		return basePath;
	}
	/**
	 * 
	 * @param projectBasePath : String
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	/**
	 * 
	 * @return resource : String[]
	 */
	public String[] getResource() {
		return resource;
	}
	/**
	 * 
	 * @param resource : String[]
	 */
	public void setResource(String[] resource) {
		this.resource = resource;
	}
	/**
	 * 
	 * @return policy : String[]
	 */
	public String[] getPolicy() {
		return policy;
	}
	/**
	 * 
	 * @param policy : String[]
	 */
	public void setPolicy(String[] policy) {
		this.policy = policy;
	}
	/**
	 * 
	 * @return app : String[]
	 */
	public String[] getApp() {
		return app;
	}
	/**
	 * 
	 * @param app : String[]
	 */
	public void setApp(String[] app) {
		this.app = app;
	}
	/**
	 * 
	 * @return owner id : String
	 */
	public String getOwnerId() {
		return ownerId;
	}
	/**
	 * 
	 * @param ownerId : String
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	/**
	 * 
	 * @return creation time : String
	 */
	public String getCreationTime() {
		return creationTime;
	}
	/**
	 * 
	 * @param creationTime : String
	 */
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	/**
	 * 
	 * @return update time : String
	 */
	public String getUpdateTime() {
		return updateTime;
	}
	/**
	 * 
	 * @param updateTime : String
	 */
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
