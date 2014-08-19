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

import eu.trentorise.smartcampus.api.manager.model.proxy.AccessApi;
import java.lang.String;
import java.util.List;

/**
 * NOT IN USE
 * Policy quota repository that add, update policy datastore for checking
 * correct access to api.
 * 
 * @author Giulia Canobbio
 *
 */
public interface AccessApiRepository extends MongoRepository<AccessApi,String>{
	/**
	 * Retrieves data by id.
	 * 
	 * @param id : String
	 * @return list of {@link AccessApi} instances
	 */
	List<AccessApi> findById(String id);
	
	/**
	 * Retrieves data searching by api and app id.
	 * 
	 * @param apiid : String
	 * @param appid : String
	 * @return list of {@link AccessApi} instances
	 */
	List<AccessApi> findByApiIdAndAppId(String apiid, String appid);
	

}
