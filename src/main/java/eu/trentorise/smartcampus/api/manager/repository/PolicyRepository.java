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

import eu.trentorise.smartcampus.api.manager.model.Policy;

import java.lang.String;
import java.util.List;

/**
 * Policy repository to add, retrieve and remove policy data.
 * 
 * @author Giulia Canobbio
 *
 */
public interface PolicyRepository extends MongoRepository<Policy, String>{

	/**
	 * Retrieves policy data searching by id.
	 * 
	 * @param id : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> findById(String id);
	
	/**
	 * Retrieves policy data searching by name.
	 * 
	 * @param name : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> findByName(String name);
	
	/**
	 * Retrieves policy data searching by category.
	 * 
	 * @param category : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> findByCategory(String category);
	
	/**
	 * Retrieves policy data searching by type.
	 * 
	 * @param type : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> findByType(String type);
}
