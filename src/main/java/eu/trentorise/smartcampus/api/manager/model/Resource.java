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



//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.*;

/**
 * Resource model
 * id : String,
 * name : String,
 * uri : String,
 * verb : String,
 * policy : list of policies,
 * creation time : String,
 * update time : String.
 * 
 * @author Giulia Canobbio
 *
 */
//@Document
public class Resource implements Serializable{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	//@Id
	private String id;
	//@Field
	//@Indexed(unique=true)
	private String name;
	//@Field
	//@Indexed(unique=true)
	private String uri;
	//@Field
	private String verb;
	//@Field
	private List<Policy> policy;
	//@Field
	private String creationTime;
	//@Field
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
	 * @return uri : String
	 */
	public String getUri() {
		return uri;
	}
	/**
	 * 
	 * @param uri : String
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	/**
	 * 
	 * @return verb : String
	 */
	public String getVerb() {
		return verb;
	}
	/**
	 * 
	 * @param verb : String
	 */
	public void setVerb(String verb) {
		this.verb = verb;
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
	 * @param policy : list of {@link Policy} instances
	 */
	public void setPolicy(List<Policy> policy) {
		this.policy = policy;
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
