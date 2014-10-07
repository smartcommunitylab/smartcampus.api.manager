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
package eu.trentorise.smartcampus.api.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import eu.trentorise.smartcampus.api.manager.model.proxy.LastTime;
import java.lang.String;
import java.util.List;

/**
 * Policy quota repository that add, update policy spike arrest datastore for checking
 * correct access to api.
 * 
 * @author Giulia Canobbio
 *
 */
public interface LastTimeRepository extends MongoRepository<LastTime,String>{
	
	/**
	 * Retrieves spike arrest apply data searching by api, resource and app id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @return list of {@link LastTime} instance
	 */
	public List<LastTime> findByApiIdAndResourceIdAndAppId(String apiId, String resourceId, String appId);
	
	/**
	 * Retrieves spike arrest apply data searching by api and resource id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return list of {@link LastTime}
	 */
	public List<LastTime> findByApiIdAndResourceId(String apiId, String resourceId);
	
}
