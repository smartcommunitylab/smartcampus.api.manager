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

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.repository.ApiRepository;
import eu.trentorise.smartcampus.api.manager.repository.AppRepository;
import eu.trentorise.smartcampus.api.manager.repository.PolicyRepository;

/**
 * Persistence manager for all entity.
 * It retrieves, remove, update and save data for 
 * Api, App, Policy and Resource.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
public class PersistenceManager {
	/**
	 * Instance of {@link ApiRepository}.
	 */
	@Autowired
	private ApiRepository apirepository;
	/**
	 * Instance of {@link AppRepository}.
	 */
	@Autowired
	private AppRepository apprepository;
	/**
	 * Instance of {@link PolicyRepository}.
	 */
	@Autowired
	private PolicyRepository policyrepository;
	
	/**
	 * Generates an id for data when id string is not set or is empty.
	 * 
	 * @return String MongoDB id
	 */
	private String generateId(){
		return new ObjectId().toString();
	}
	
	/* 
	 *  Api
	 */
	
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
	 * Retries Api data searching by base path.
	 * 
	 * @param basePath : String
	 * @return list of instance of {@link Api}
	 */
	public List<Api> getApiByBasePath(String basePath){
		return (List<Api>) apirepository.findByBasePath(basePath);
	}
	
	/**
	 * Retrieves Api data searching by owner id.
	 * 
	 * @param ownerId : String
	 * @return list of instance of {@link Api}
	 */
	public List<Api> getApiByOwnerId(String ownerId){
		return (List<Api>) apirepository.findByOwnerId(ownerId);
	}
	
	/**
	 * Create a new Api data and save it in db.
	 * Before this method checks if Api object
	 * is filled correctly, otherwise
	 * it throws IllegalArgumentException.
	 * The required fields are: name, basePath and ownerId.
	 * 
	 * @param api : instance of {@link Api}
	 * @return new instance of {@link Api}
	 */
	public Api addApi(Api api){
		if(api.getName()==null || api.getBasePath()==null || api.getOwnerId()==null){
			throw new IllegalArgumentException("Api name, base path and owner id are required.");
		}
		if(api.getId()==null || api.getId().equalsIgnoreCase("")){
			api.setId(generateId());
		}
		Date today = new Date();
		api.setCreationTime(today.toString());
		return apirepository.save(api);
	}
	
	/**
	 * Update an existing Api instance.
	 * Before this method checks if Api object
	 * is filled correctly, otherwise
	 * it throws IllegalArgumentException.
	 * The required fields are: name, basePath and ownerId.
	 * 
	 * @param api : instance of {@link Api}
	 * @return updated instance of {@link Api}
	 */
	public Api updateApi(Api api){
		if(api.getName()==null || api.getBasePath()==null || api.getOwnerId()==null){
			throw new IllegalArgumentException("Api name, base path and owner id are required.");
		}
		Date today = new Date();
		api.setUpdateTime(today.toString());
		return apirepository.save(api);
	}
	
	/**
	 * Deletes Api data from db.
	 * 
	 * @param api : instance of {@link Api}
	 */
	public void deleteApi(Api api){
		apirepository.delete(api);
	}
	
	/* 
	 *  App
	 */
	
	/**
	 * Retrieves all App data saved in db.
	 * 
	 * @return list of {@link App} instances
	 */
	public List<App> listApp(){
		return apprepository.findAll();
	}
	
	/**
	 * Retrieves App data searching by id.
	 * 
	 * @param id : String
	 * @return instance of {@link App}
	 */
	public App getAppById(String id){
		return (App) apprepository.findById(id).get(0);
	}
	
	/**
	 * Retrieves App data searching by key.
	 * 
	 * @param key : String
	 * @return list of {@link App} instances
	 */
	public List<App> getAppByKey(String key){
		return (List<App>) apprepository.findByKey(key);
	}
	
	/**
	 * Retrieves App data searching by name.
	 * 
	 * @param name : String
	 * @return list of {@link App}
	 */
	public List<App> getAppByName(String name){
		return (List<App>) apprepository.findByName(name);
	}
	
	/**
	 * Create a new App and save it in db.
	 * If name field is null then throws IllegalArgumentException, because
	 * name is required.
	 * 
	 * @param app : instance of {@link App}
	 * @return saved instance of {@link App}
	 */
	public App addApp(App app){
		if(app.getName()==null){
			throw new IllegalArgumentException("App name is required.");
		}
		if(app.getId()==null || app.getId().equalsIgnoreCase("")){
			app.setId(generateId());
		}
		return apprepository.save(app);
	}
	
	/**
	 * Update an existing App.
	 * 
	 * @param app : instance of {@link App}
	 * @return updated instace of {@link App}
	 */
	public App updateApp(App app){
		return addApp(app);
	}
	
	/**
	 * Deletes an App from db.
	 * 
	 * @param app : instance of {@link App}
	 */
	public void deleteApp(App app){
		apprepository.delete(app);
	}
	
	/* 
	 * Policy 
	 */
	
	/**
	 * Retrieves all policy data saved in db.
	 * 
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> listPolicy(){
		return policyrepository.findAll();
	}
	
	/**
	 * Retrieves policy data searching by id.
	 * 
	 * @param id : String
	 * @return instance of {@link Policy}
	 */
	public Policy getPolicyById(String id){
		return (Policy) policyrepository.findById(id).get(0);
	}
	
	/**
	 * Retrieves policy data searching by name.
	 * 
	 * @param name : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> getPolicyByName(String name){
		return (List<Policy>) policyrepository.findByName(name);
	}
	
	/**
	 * Retrieves policy data searching by category.
	 * 
	 * @param category : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> getPolicyByCategory(String category){
		return (List<Policy>) policyrepository.findByCategory(category);
	}
	
	/**
	 * Retrieves policy data searching by type.
	 * 
	 * @param type : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> getPolicyByType(String type){
		return (List<Policy>) policyrepository.findByType(type);
	}
	
	/**
	 * Create a new Policy in db.
	 * If name or type field are undefined then it throws 
	 * IllegalArgumentException, because they are required.
	 * 
	 * @param p : instance of {@link Policy}
	 * @return saved instance of {@link Policy}
	 */
	public Policy addPolicy(Policy p){
		if(p.getName()==null || p.getType()==null){
			throw new IllegalArgumentException("Policy name and type are required.");
		}
		if(p.getId()==null || p.getId().equalsIgnoreCase("")){
			p.setId(generateId());
		}
		return policyrepository.save(p);
	}
	
	/**
	 * Update a policy saved in db.
	 * 
	 * @param p : instance of {@link Policy}
	 * @return updated instance of {@link Policy}
	 */
	public Policy updatePolicy(Policy p){
		return addPolicy(p);
	}
	
	/**
	 * Delete a policy from db.
	 * 
	 * @param p : instance of {@link Policy}
	 */
	public void deletePolicy(Policy p){
		policyrepository.delete(p);
	}

}
