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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User:
 * id: string;
 * username: string, unique index;
 * password: string;
 * email: string, unique index;
 * enabled: int, value 0 or 1;
 * profile: list;
 * role: string;
 * gatrackid: string
 * 
 * @author Giulia Canobbio
 *
 */
@Document
public class User implements Serializable{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	@Field
	@Indexed(unique=true)
	private String username;
	@Field
	private String password;
	@Field
	@Indexed(unique=true)
	private String email;
	@Field
	private int enabled;
	@Field
	private Profile profile;
	@Field
	private String role;
	@Field
	private String gatrackid;

	/**
	 * Get user id.
	 * 
	 * @return String user id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set user id.
	 * 
	 * @param id : String
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get username.
	 * 
	 * @return String username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set username.
	 * 
	 * @param username : String
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get password.
	 * 
	 * @return String password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set password.
	 * 
	 * @param password : String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get email address.
	 * 
	 * @return String email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set email address.
	 * 
	 * @param email : String
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get enabled value.
	 * Possible values are: 
	 * 0 - if user is not enabled,
	 * 1- if user is enabled.
	 * 
	 * @return int enabled
	 */
	public int getEnabled() {
		return enabled;
	}

	/**
	 * Set enabled value.
	 * Possible values are:
	 * 0 - if user is not enabled,
	 * 1 - if user is enabled.
	 * 
	 * @param enabled : int
	 */
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Get ser profile.
	 * 
	 * @return {@link Profile} instance 
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * Set user profile.
	 * 
	 * @param profile : {@link Profile}
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * Get user role.
	 * Role can be: provider.
	 * 
	 * @return String role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Set user role.
	 * Role can be: provider.
	 * 
	 * @param role : String
	 */
	public void setRole(String role) {
		this.role = role;
	}
	/**
	 * 
	 * @return String google analytics tracking id
	 */
	public String getGatrackid() {
		return gatrackid;
	}
	/**
	 * 
	 * @param gatrackid : String, google analytics tracking id
	 */
	public void setGatrackid(String gatrackid) {
		this.gatrackid = gatrackid;
	}
	
}
