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
 * backend service URL: String,
 * project base path: String.
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
	@Field("url")
	private String backendServiceURL;
	@Field("path")
	private String projectBasePath;
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
	 * @return backend service url : String
	 */
	public String getBackendServiceURL() {
		return backendServiceURL;
	}
	/**
	 * 
	 * @param backendServiceURL : String
	 */
	public void setBackendServiceURL(String backendServiceURL) {
		this.backendServiceURL = backendServiceURL;
	}
	/**
	 * 
	 * @return project base path : String
	 */
	public String getProjectBasePath() {
		return projectBasePath;
	}
	/**
	 * 
	 * @param projectBasePath : String
	 */
	public void setProjectBasePath(String projectBasePath) {
		this.projectBasePath = projectBasePath;
	}
	
	
}
