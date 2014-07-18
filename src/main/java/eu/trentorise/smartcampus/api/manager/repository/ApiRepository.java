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

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import eu.trentorise.smartcampus.api.manager.model.Api;
import java.lang.String;

/**
 * Api repository add, find and remove api collections.
 * 
 * @author Giulia Canobbio
 *
 */
public interface ApiRepository extends MongoRepository<Api,String>{
	
	/**
	 * Retrieves Api data searching by id.
	 * 
	 * @param id : String
	 * @return list of {@link Api}
	 */
	public List<Api> findById(String id);
	
	/**
	 * Retrieves Api data searching by id.
	 * 
	 * @param basepath : String
	 * @return list of {@link Api}
	 */
	public List<Api> findByBasePath(String basepath);
	
	/**
	 * Retrieves Api data searching by owner id.
	 * 
	 * @param ownerid : String
	 * @return list of {@link Api}
	 */
	public List<Api> findByOwnerId(String ownerid);
	
}
