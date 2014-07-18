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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.repository.ApiRepository;

/**
 * Persistence manager for Api entity.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
public class ApiManager {
	/**
	 * Instance of {@link ApiRepository}.
	 */
	@Autowired
	private ApiRepository apirepository;
	
	/**
	 * Create a new Api data and save it in db.
	 * 
	 * @param api : instance of {@link Api}
	 * @return new instance of {@link Api}
	 */
	public Api addApi(Api api){
		return apirepository.save(api);
	}
	
	/**
	 * Retrieve all Api data saved in db.
	 * 
	 * @return list of {@link Api} instance
	 */
	public List<Api> listApi(){
		return apirepository.findAll();
	}
	
	/**
	 * Retrieves Api data searching by id.
	 * 
	 * @param id : String
	 * @return instance of {@link Api}
	 */
	public Api getApiById(String id){
		return (Api) apirepository.findById(id).get(0);
	}
	
	/**
	 * Deletes Api data from db.
	 * 
	 * @param api : instance of {@link Api}
	 */
	public void deleteApi(Api api){
		apirepository.delete(api);
	}

}
