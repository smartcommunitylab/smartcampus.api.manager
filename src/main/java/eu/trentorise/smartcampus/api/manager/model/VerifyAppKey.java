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

//import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Policy model of Verify App Key
 * anonymous : boolean, variable used to check if user allows anonymous access to api.
 * 
 * @author Giulia Canobbio
 *
 */
//@Document
public class VerifyAppKey extends Policy{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	//@Field
	private boolean anonymous;

	/**
	 * 
	 * @return if anonymous access is allowed then true else false
	 */
	public boolean isAnonymous() {
		return anonymous;
	}

	/**
	 * 
	 * @param anonymous : boolean, allow anonymous access
	 */
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	

}
