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
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Api model
 * id: String,
 * name: String,
 * base path: String,
 * resource: list of resources,
 * policy: list of policies,
 * app : list of app,
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
	@Field
	@Indexed(unique=true)
	private String name;
	@Field
	@Indexed(unique=true)
	private String basePath;
	@Field
	private List<Resource> resource;
	@Field
	private List<Policy> policy;
	/*@Field
	private List<App> app;*/
	@Field
	private String ownerId;
	@Field
	private String creationTime;
	@Field
	private String updateTime;
	@Field
	private List<Status> status;
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
	 * @return resource : list of {@link Resource} instances
	 */
	public List<Resource> getResource() {
		return resource;
	}
	/**
	 * 
	 * @param resource : {@link Resource} instance
	 */
	public void setResource(List<Resource> resource) {
		this.resource = resource;
	}
	/**
	 * 
	 * @return policy : list of {@link Policy} instances
	 */
	public List<Policy> getPolicy() {
		return policy;
	}
	/**
	 * 
	 * @param policy : {@link Policy} instance
	 */
	public void setPolicy(List<Policy> policy) {
		this.policy = policy;
	}
	/**
	 * 
	 * @return app : list of {@link App} instances
	 */
	/*public List<App> getApp() {
		return app;
	}*/
	/**
	 * 
	 * @param app :{@link Resource} instance
	 */
	/*public void setApp(List<App> app) {
		this.app = app;
	}*/
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
	/**
	 * 
	 * @return status, list of string
	 */
	public List<Status> getStatus() {
		return status;
	}
	/**
	 * 
	 * @param status : list of string
	 */
	public void setStatus(List<Status> status) {
		this.status = status;
	}
}
