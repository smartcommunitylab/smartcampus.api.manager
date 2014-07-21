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

import eu.trentorise.smartcampus.api.manager.model.Resource;
import java.lang.String;
import java.util.List;

/**
 * Resource repository to add, retrieve, update and delte resource data.
 * 
 * @author Giulia Canobbio
 *
 */
public interface ResourceRepository extends MongoRepository<Resource, String>{
	
	/**
	 * Retrieves Resource data searching by id.
	 * 
	 * @param id : String
	 * @return list of {@link Resource} instances
	 */
	public List<Resource> findById(String id);
	
	/**
	 * Retrieves Resource data searching by name.
	 * 
	 * @param name : String
	 * @return list of {@link Resource} instances
	 */
	public List<Resource> findByName(String name);
	
	

}
