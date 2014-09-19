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

package eu.trentorise.smartcampus.api.manager.persistence;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class that checks if current user is the same that want to
 * do some actions such as update, delete or retrieve data from db.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
public class PermissionManager {
	
	/**
	 * Retrieves name of current user from spring context.
	 * 
	 * @return name of user : String
	 */
	public String getUsername(){
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	/**
	 * Function that compares name of current user with a
	 * parameter id.
	 * We assume that user name is user id in our db for now
	 * TODO
	 * 
	 * @param id : String
	 * @return boolean value: if user name is equal to id then true,
	 * 			otherwise false.
	 */
	public boolean canUserDoThisOperation(String id){
		String user = getUsername();
		
		//Username is saved in db as ownerId (FOR NOW)
		if(user.equalsIgnoreCase(id)){
			return true;
		}
		return false;
	}

}
