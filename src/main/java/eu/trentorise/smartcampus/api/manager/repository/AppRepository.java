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

import eu.trentorise.smartcampus.api.manager.model.App;
import java.lang.String;
import java.util.List;

/**
 * App repository to add, retrieve or remove app data.
 * 
 * @author Giulia Canobbio
 *
 */
public interface AppRepository extends MongoRepository<App,String>{
	/**
	 * Retrieves App data searching by id.
	 * 
	 * @param id : String
	 * @return list of {@link App} instances
	 */
	public List<App> findById(String id);
	
	/**
	 * Retrieves App data searching by key.
	 * 
	 * @param key : String
	 * @return list of {@link App} instances
	 */
	public List<App> findByKey(String key);
	
	/**
	 * Retrieves App data searching by name.
	 * 
	 * @param name : String
	 * @return list of  {@link App} instances
	 */
	public List<App> findByName(String name);

}
