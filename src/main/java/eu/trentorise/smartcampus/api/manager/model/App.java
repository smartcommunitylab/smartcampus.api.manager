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
 * App model
 * id : String,
 * name : String,
 * key : String,
 * apis : collection of api id and api status.
 * 
 * @author Giulia Canobbio
 *
 */
@Document
public class App implements Serializable{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	@Field("name")
	@Indexed(unique=true)
	private String name;
	@Field
	@Indexed(unique=true)
	private String key;
	@Field
	private List<ApiData> apis;
	@Field
	private String ownerId;
	
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
	 * @return key : String
	 */
	public String getKey() {
		return key;
	}
	/**
	 * 
	 * @param key : String
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * 
	 * @return list of {@link ApiData} instances
	 */
	public List<ApiData> getApis() {
		return apis;
	}
	/**
	 * 
	 * @param apis : list of {@link ApiData} instances
	 */
	public void setApis(List<ApiData> apis) {
		this.apis = apis;
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
	

}
