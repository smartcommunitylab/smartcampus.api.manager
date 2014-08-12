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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.Constants;
import eu.trentorise.smartcampus.api.manager.Constants.POLICY_CATEGORY;
import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.Status;
import eu.trentorise.smartcampus.api.manager.repository.ApiRepository;
import eu.trentorise.smartcampus.api.manager.repository.AppRepository;
import eu.trentorise.smartcampus.api.manager.repository.PolicyRepository;
import eu.trentorise.smartcampus.api.manager.repository.ResourceRepository;
import eu.trentorise.smartcampus.api.manager.util.UriValidation;

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
	 * Instance of {@link ResourceRepository}.
	 */
	@Autowired
	private ResourceRepository resourcerepository;
	
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
		List<Api> api = apirepository.findById(id);
		if(api!=null && api.size()>0){
			return api.get(0);
		}
		return null;
	}
	
	/**
	 * Retrieves Api data searching by name.
	 * 
	 * @param name : String
	 * @return instance of {@link Api}
	 */
	public List<Api> getApiByName(String name){
		return (List<Api>) apirepository.findByName(name);
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
		//check name
		List<Api> savedApiName = getApiByName(api.getName());
		if(savedApiName!=null && savedApiName.size()>0){
			throw new IllegalArgumentException("This name is already saved. " +
					"For adding an api, change it");
		}
		//check api basepath
		List<Api> savedApi = getApiByBasePath(api.getBasePath());
		if(savedApi!=null && savedApi.size()>0){
			throw new IllegalArgumentException("This base path is already saved. " +
					"For adding an api, change it");
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
		if(api.getId()==null || api.getId().equalsIgnoreCase("")){
			throw new IllegalArgumentException("Api with undefined id does not exist.");
		}
		if(api.getName()==null || api.getBasePath()==null || api.getOwnerId()==null){
			throw new IllegalArgumentException("Api name, base path and owner id are required.");
		}
		//check api basepath
		List<Api> savedApi = getApiByBasePath(api.getBasePath());
		if(savedApi!=null && savedApi.size()>0){
			if(!savedApi.get(0).getId().equalsIgnoreCase(api.getId())){
				throw new IllegalArgumentException("This base path is already " +
						"saved for a different api. For adding an api, change it");
			}
		}
		Date today = new Date();
		api.setUpdateTime(today.toString());
		return apirepository.save(api);
	}
	
	/**
	 * Update an existing Api instance. The only api parameters that user can modify are
	 * name and basepath.
	 * This method throws an exception when api id, name, base path and 
	 * owner id are not defined, because they are required.
	 * 
	 * @param api : instance of {@link Api}
	 * @return updated instance of {@link Api}
	 */
	public Api updateApiParameter(Api api){
		if(api.getId()==null || api.getId().equalsIgnoreCase("")){
			throw new IllegalArgumentException("Api with undefined id does not exist.");
		}
		if(api.getName()==null || api.getBasePath()==null || api.getOwnerId()==null){
			throw new IllegalArgumentException("Api name, base path and owner id are required.");
		}
		//retrieve Api
		Api savedApi = getApiById(api.getId());
		//set name and basepath if different
		if(!savedApi.getName().equalsIgnoreCase(api.getName()) || 
				!savedApi.getBasePath().equalsIgnoreCase(api.getBasePath())){
			savedApi.setName(api.getName());
			savedApi.setBasePath(api.getBasePath());
		}
		//updated time
		Date today = new Date();
		savedApi.setUpdateTime(today.toString());
		return apirepository.save(savedApi);
	}
	
	/**
	 * Deletes Api data from db.
	 * 
	 * @param api : instance of {@link Api}
	 */
	public void deleteApi(Api api){
		apirepository.delete(api);
	}
	
	/**
	 * Deletes Api data from db using api id.
	 * 
	 * @param apiId : String
	 */
	public void deleteApi(String apiId){
		apirepository.delete(apiId);
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
		List<App> apps = apprepository.findById(id);
		if(apps!=null && apps.size()>0){
			return apps.get(0);
		}
		return null;
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
		//set app key
		app.setKey(UUID.randomUUID().toString());
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
	 * @param appId : String
	 */
	public void deleteApp(String appId){
		apprepository.delete(appId);
	}
	
	/* 
	 * Policy 
	 */
	
	/**
	 * Retrieves all policy data saved in db.
	 * NOT IN USE
	 * 
	 * @return list of {@link Policy} instances
	 */
	/*public List<Policy> listPolicy(){
		return policyrepository.findAll();
	}
	*/
	/**
	 * Retrieves policy data searching by id.
	 * NOT IN USE
	 * 
	 * @param id : String
	 * @return instance of {@link Policy}
	 */
	/*public Policy getPolicyById(String id){
		List<Policy> ps = policyrepository.findById(id);
		if(ps!=null && ps.size()>0){
			return ps.get(0);
		}
		return null;
	}
	*/
	/**
	 * Retrieves policy data searching by name.
	 * NOT IN USE
	 * 
	 * @param name : String
	 * @return list of {@link Policy} instances
	 */
	/*public List<Policy> getPolicyByName(String name){
		return (List<Policy>) policyrepository.findByName(name);
	}
	*/
	/**
	 * Retrieves policy data searching by category.
	 * NOT IN USE
	 * 
	 * @param category : String
	 * @return list of {@link Policy} instances
	 */
	/*public List<Policy> getPolicyByCategory(String category){
		return (List<Policy>) policyrepository.findByCategory(category);
	}
	*/
	/**
	 * Retrieves policy data searching by type.
	 * NOT IN USE
	 * 
	 * @param type : String
	 * @return list of {@link Policy} instances
	 */
	/*public List<Policy> getPolicyByType(String type){
		return (List<Policy>) policyrepository.findByType(type);
	}
	*/
	/**
	 * Create a new Policy in db.
	 * If name or type field are undefined then it throws 
	 * IllegalArgumentException, because they are required.
	 * NOT IN USE
	 * 
	 * @param p : instance of {@link Policy}
	 * @return saved instance of {@link Policy}
	 */
	/*public Policy addPolicy(Policy p){
		if(p.getName()==null || p.getType()==null){
			throw new IllegalArgumentException("Policy name and type are required.");
		}
		isPolicyInstanceOf(p);
		if(p.getId()==null || p.getId().equalsIgnoreCase("")){
			p.setId(generateId());
		}
		if(p instanceof SpikeArrest || p instanceof Quota){
			p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
		}
		return policyrepository.save(p);
	}
	*/
	/**
	 * Update a policy saved in db.
	 * NOT IN USE
	 * 
	 * @param p : instance of {@link Policy}
	 * @return updated instance of {@link Policy}
	 */
	/*public Policy updatePolicy(Policy p){
	 	if(p instanceof SpikeArrest || p instanceof Quota){
			p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
		}
		return addPolicy(p);
	}
	*/
	/**
	 * Delete a policy from db.
	 * NOT IN USE
	 * 
	 * @param p : instance of {@link Policy}
	 */
	/*public void deletePolicy(Policy p){
		policyrepository.delete(p);
	}
	*/
	/*
	 * Resource
	 */
	
	/**
	 * Retrieves all resources saved in db.
	 * NOT IN USE
	 * 
	 * @return list of {@link Resource} instances
	 */
	/*public List<Resource> listResource(){
		return resourcerepository.findAll();
	}
	*/
	/**
	 * Retrieves resource data searching by id.
	 * NOT IN USE
	 * 
	 * @param id : String
	 * @return instance of {@link Resource}
	 */
	/*public Resource getResourceById(String id){
		List<Resource> rs = resourcerepository.findById(id);
		if(rs!=null && rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	*/
	/**
	 * Retrieves resource data searching by name.
	 * NOT IN USE
	 * 
	 * @param name : String
	 * @return list of {@link Resource} instances
	 */
	/*public List<Resource> getResourceByName(String name){
		return (List<Resource>) resourcerepository.findByName(name);
	}
	*/
	/**
	 * Create a new resource and saved it in db.
	 * If name, uri or verb are undefined then it throws IllegalArgumentException,
	 * because they are required.
	 * NOT IN USE
	 * 
	 * @param r : instance of {@link Resource}
	 * @return saved instance of {@link Resource}
	 */
	/*public Resource addResource(Resource r){
		if(r.getName()==null || r.getUri()==null || r.getVerb()==null){
			throw new IllegalArgumentException("Resource name, uri and verb are required.");
		}
		
		if(r.getId()==null || r.getId().equalsIgnoreCase("")){
			r.setId(generateId());
		}
		
		if(r.getVerb().equalsIgnoreCase("GET")){
			r.setVerb(Constants.VERB.GET.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("POST")){
			r.setVerb(Constants.VERB.POST.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("PUT")){
			r.setVerb(Constants.VERB.PUT.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("DELETE")){
			r.setVerb(Constants.VERB.DELETE.toString());
		}
		else{
			throw new IllegalArgumentException("Resource verb values can be GET, POST, PUT or DELETE.");
		}
		
		UrlValidator urlValidator = new UrlValidator();
		if(!urlValidator.isValid(r.getUri())){
			throw new IllegalArgumentException("Uri is not valid.");
		}
		
		Date today = new Date();
		r.setCreationTime(today.toString());
		return resourcerepository.save(r);
	}
	*/
	/**
	 * Update a resource in db.
	 * NOT IN USE
	 * 
	 * @param r : instance of {@link Resource}
	 * @return updated instance of {@link Resource}
	 */
	/*public Resource updateResource(Resource r){
		if(r.getName()==null || r.getUri()==null || r.getVerb()==null){
			throw new IllegalArgumentException("Resource name, uri and verb are required.");
		}
		
		if(r.getVerb().equalsIgnoreCase("GET")){
			r.setVerb(Constants.VERB.GET.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("POST")){
			r.setVerb(Constants.VERB.POST.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("PUT")){
			r.setVerb(Constants.VERB.PUT.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("DELETE")){
			r.setVerb(Constants.VERB.DELETE.toString());
		}
		else{
			throw new IllegalArgumentException("Resource verb values can be GET, POST, PUT or DELETE.");
		}
		
		UrlValidator urlValidator = new UrlValidator();
		if(!urlValidator.isValid(r.getUri())){
			throw new IllegalArgumentException("Uri is not valid.");
		}
		
		Date today = new Date();
		r.setUpdateTime(today.toString());
		return resourcerepository.save(r);
	}
	*/
	/**
	 * Delete an existing resource in db.
	 * NOT IN USE
	 * 
	 * @param r : instance of {@link Resource}
	 */
	/*public void deleteResource(Resource r){
		resourcerepository.delete(r);
	}
	*/
	
	/*
	 * API retrieves Resource and Policy data
	 */
	
	/**
	 * Add resource to an Api instance.
	 * First this method checks if name, uri and verb are undefined, otherwise it 
	 * throws IllegalArgumentException.
	 * Then it checks if uri resource is valid otherwise it throws the same exception with different
	 * message.
	 * After that it retrieves api data searching by id and update it, adding
	 * the new resource.
	 * 
	 * @param apiId : String
	 * @param r : instance of {@link Resource}
	 * @return added instance of {@link Resource}
	 */
	public Resource addResourceApi(String apiId, Resource r){
		//check resource fields
		if(r.getName()==null || r.getUri()==null || r.getVerb()==null){
			throw new IllegalArgumentException("Resource name, uri and verb are required.");
		}
		
		if(resourceApiExists(apiId, r.getName())){
			throw new IllegalArgumentException("Resource already exists. Change name.");
		}
		if(resourceUriApiExists(apiId, r.getUri())){
			throw new IllegalArgumentException("Resource already exists. Change uri.");
		}

		if(r.getId()==null || r.getId().equalsIgnoreCase("")){
			r.setId(generateId());
		}
		
		if(r.getVerb().equalsIgnoreCase("GET")){
			r.setVerb(Constants.VERB.GET.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("POST")){
			r.setVerb(Constants.VERB.POST.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("PUT")){
			r.setVerb(Constants.VERB.PUT.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("DELETE")){
			r.setVerb(Constants.VERB.DELETE.toString());
		}
		else{
			throw new IllegalArgumentException("Resource verb values can be GET, POST, PUT or DELETE.");
		}
		
		/*UrlValidator urlValidator = new UrlValidator();
		if(!urlValidator.isValid(r.getUri())){
			throw new IllegalArgumentException("Uri is not valid.");
		}*/
		UriValidation uriValidator = new UriValidation();
		if(!uriValidator.validate(r.getUri())){
			throw new IllegalArgumentException("Uri is not valid.");
		}
		
		Date today = new Date();
		r.setCreationTime(today.toString());
		
		//get api and add resource
		Api api = getApiById(apiId);
		List<Resource> rlist = api.getResource();
		if(rlist!=null){
			rlist.add(r);
		}else{
			List<Resource> rs = new ArrayList<Resource>();
			rs.add(r);
			api.setResource(rs);
		}
		
		//update api
		updateApi(api);
		
		return getResourceApiByResourceId(apiId, r.getId());
	}
	
	/**
	 * Updates a resource in Api instance.
	 * First this method checks if name, uri and verb are undefined, otherwise it 
	 * throws IllegalArgumentException.
	 * Then it checks if uri resource is valid otherwise it throws the same exception with different
	 * message.
	 * After that it retrieves api data searching by id and update it, adding
	 * the new resource.
	 * Then it retrieves the wanted resource by its id and sets fields.
	 * The it updates api.
	 * 
	 * @param apiId : String 
	 * @param r : instance of {@link Resource}
	 * @return updated instance of {@link Resource}
	 */
	public Resource updateResourceApi(String apiId, Resource r){
		//check resource fields
		if(r.getName()==null || r.getUri()==null || r.getVerb()==null){
			throw new IllegalArgumentException("Resource name, uri and verb are required.");
		}
		
		if(r.getVerb().equalsIgnoreCase("GET")){
			r.setVerb(Constants.VERB.GET.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("POST")){
			r.setVerb(Constants.VERB.POST.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("PUT")){
			r.setVerb(Constants.VERB.PUT.toString());
		}
		else if(r.getVerb().equalsIgnoreCase("DELETE")){
			r.setVerb(Constants.VERB.DELETE.toString());
		}
		else{
			throw new IllegalArgumentException("Resource verb values can be GET, POST, PUT or DELETE.");
		}
		
		
		/*UrlValidator urlValidator = new UrlValidator();
		if(!urlValidator.isValid(r.getUri())){
			throw new IllegalArgumentException("Uri is not valid.");
		}*/
		UriValidation uriValidator = new UriValidation();
		if(!uriValidator.validate(r.getUri())){
			throw new IllegalArgumentException("Uri is not valid.");
		}
		
		//retrieve api searching by id
		Api api = getApiById(apiId);
		//retrieve resource
		List<Resource> rlist = api.getResource();
		Resource oldr = null;
		for(int i=0;i<rlist.size();i++){
			if(rlist.get(i).getId().equalsIgnoreCase(r.getId())){
				oldr = rlist.get(i);
			}
		}
		if(oldr!=null){
			//check new name
			if(!oldr.getName().equalsIgnoreCase(r.getName())){
				if(resourceApiExists(apiId, r.getName())){
					throw new IllegalArgumentException("Resource already exists. " +
							"Change name.");
				}
			}
			oldr.setName(r.getName());
			//check new uri
			if (!oldr.getUri().equalsIgnoreCase(r.getUri())) {
				if (resourceUriApiExists(apiId, r.getUri())) {
					throw new IllegalArgumentException("Resource already exists. " +
							"Change uri.");
				}
			}
			oldr.setUri(r.getUri());
			oldr.setVerb(r.getVerb());
			if(r.getPolicy()!=null){
				oldr.setPolicy(r.getPolicy());
			}
			Date today = new Date();
			oldr.setUpdateTime(today.toString());
		}
		//update api
		updateApi(api);
		
		return getResourceApiByResourceId(apiId, r.getId());
	}
	
	/**
	 * Deletes a resource from Api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 */
	public void deleteResourceApi(String apiId, String resourceId){
		//retrieves api
		Api api = getApiById(apiId);
		//retrieves resource
		List<Resource> rlist = api.getResource();

		if(rlist!=null){
			for(int i=0; i<rlist.size();i++){
				if(rlist.get(i).getId().equalsIgnoreCase(resourceId)){
				//delete resource
				rlist.remove(i);
				}
			}
		}
		api.setResource(rlist);
		updateApi(api);
	}
	
	/**
	 * Retrieves resource data from Api searching by resource id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link Resouce}
	 */
	public Resource getResourceApiByResourceId(String apiId, String resourceId){
		Api api = getApiById(apiId);
		try {
			List<Resource> rlist = api.getResource();
			Resource r = null;

			if (rlist != null) {
				for (int i = 0; i < rlist.size(); i++) {
					if (rlist.get(i).getId().equalsIgnoreCase(resourceId)) {
						r = rlist.get(i);
					}
				}
			}
			return r;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieves resource data from Api searching by resource name.
	 * 
	 * @param apiId : String
	 * @param resourceName : String
	 * @return list of {@link Resource} instances
	 */
	public List<Resource> getResourceApiByResourceName(String apiId, String resourceName){
		Api api = getApiById(apiId);
		try {
			List<Resource> rlist = api.getResource();
			List<Resource> rs = new ArrayList<Resource>();

			if (rlist != null) {
				for (int i = 0; i < rlist.size(); i++) {
					if (rlist.get(i).getName().equalsIgnoreCase(resourceName)) {
						rs.add(rlist.get(i));
					}
				}
			}
			return rs;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieves resource data from Api searching by resource uri.
	 * 
	 * @param apiId : String
	 * @param resourceName : String
	 * @return list of {@link Resource} instances
	 */
	public List<Resource> getResourceApiByResourceUri(String apiId, String resourceUri){
		Api api = getApiById(apiId);
		try {
			List<Resource> rlist = api.getResource();
			List<Resource> rs = new ArrayList<Resource>();

			if (rlist != null) {
				for (int i = 0; i < rlist.size(); i++) {
					if (rlist.get(i).getUri().equalsIgnoreCase(resourceUri)) {
						rs.add(rlist.get(i));
					}
				}
			}
			return rs;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Add policy to an Api instance.
	 * First this method checks if name and type are undefined, otherwise it 
	 * throws IllegalArgumentException.
	 * After that it retrieves api data searching by id and update it, adding
	 * the new policy.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Policy}
	 * @return added instance of {@link Policy}
	 */
	public Policy addPolicyApi(String apiId, Policy p){
		//checks policy fields
		if(p.getName()==null || p.getType()==null){
			throw new IllegalArgumentException("Policy name and type are required.");
		}
		isPolicyInstanceOf(p);
		if(p.getId()==null || p.getId().equalsIgnoreCase("")){
			p.setId(generateId());
		}
		if(policyApiExists(apiId, p.getName())){
			throw new IllegalArgumentException("Policy with this name already exists.");
		}
		if(p instanceof SpikeArrest || p instanceof Quota){
			p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
		}
		// get api and add policy
		Api api = getApiById(apiId);
		List<Policy> plist = (List<Policy>) api.getPolicy();
		if (plist != null) {
			plist.add(p);
		} else {
			List<Policy> ps = new ArrayList<Policy>();
			ps.add(p);
			api.setPolicy(ps);
		}

		// update api
		updateApi(api);
		
		return getPolicyApiByPolicyId(apiId, p.getId());
	}
	
	/**
	 * Updates a resource in Api instance.
	 * First this method checks if name and type are undefined, otherwise it 
	 * throws IllegalArgumentException.
	 * After that it retrieves api data searching by id and update it, adding
	 * the new policy.
	 * Then it retrieves the wanted policy by its id and sets fields.
	 * It updates api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Policy}
	 * @return updated instance of {@link Policy}
	 */
	public Policy updatePolicyApi(String apiId, Policy p){
		//checks policy fields
		if(p.getName()==null || p.getType()==null){
			throw new IllegalArgumentException("Policy name and type are required.");
		}
		isPolicyInstanceOf(p);
		//retrieve api searching by id
		//check category
		if(p instanceof SpikeArrest || p instanceof Quota){
			if(!p.getCategory().equalsIgnoreCase(POLICY_CATEGORY.QualityOfService.toString())){
				p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
				//throw new IllegalArgumentException("Policy category is wrong.");
			}
		}
		Api api = getApiById(apiId);
		//retrieve policy
		List<Policy> plist = (List<Policy>) api.getPolicy();
		Policy oldp = null;
		for(int i=0;i<plist.size();i++){
			if(plist.get(i).getId().equalsIgnoreCase(p.getId())){
				oldp = plist.get(i);
				plist.remove(i);
			}
		}
		if(oldp!=null){
			if(!oldp.getName().equalsIgnoreCase(p.getName())){
				if(policyApiExists(apiId, p.getName())){
					throw new IllegalArgumentException("Policy with this name already exists.");
				}
			}
			plist.add(p);
		}
		//update api
		updateApi(api);
		
		return getPolicyApiByPolicyId(apiId, p.getId());
	}
	
	/**
	 * Deletes policy from Api.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 */
	public void deletePolicyApi(String apiId, String policyId){
		// retrieves api
		Api api = getApiById(apiId);
		// retrieves policy
		List<Policy> plist = (List<Policy>) api.getPolicy();

		if (plist != null) {
			for (int i = 0; i < plist.size(); i++) {
				if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
					// delete resource
					plist.remove(i);
				}
			}
		}
		api.setPolicy(plist);
		updateApi(api);
	}
	
	/**
	 * Retrieves policy data from Api searching by policy id.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link Policy}
	 */
	public Policy getPolicyApiByPolicyId(String apiId, String policyId){
		Api api = getApiById(apiId);
		try {
			List<Policy> plist = (List<Policy>) api.getPolicy();
			Policy p = null;

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
						p = plist.get(i);
					}
				}
			}
			return p;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieves spike arrest policy data from Api searching by policy id.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link SpikeArrest}
	 */
	public Policy getSpikeArrestPolicyApiByPolicyId(String apiId, String policyId){
		Api api = getApiById(apiId);
		try {
			List<Policy> plist = api.getPolicy();
			SpikeArrest p = null;

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
						p = (SpikeArrest) plist.get(i);
					}
				}
			}
			return p;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieves quota data from Api searching by policy id.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link Quota}
	 */
	public Policy getQuotaPolicyApiByPolicyId(String apiId, String policyId){
		Api api = getApiById(apiId);
		try {
			List<Policy> plist = api.getPolicy();
			Quota p = null;

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
						p = (Quota) plist.get(i);
					}
				}
			}
			return p;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieves policy data from Api searching by policy name.
	 * 
	 * @param apiId : String
	 * @param policyName : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> getPolicyApiByPolicyName(String apiId, String policyName){
		Api api = getApiById(apiId);
		try {
			List<Policy> plist = (List<Policy>) api.getPolicy();
			List<Policy> ps = new ArrayList<Policy>();

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getName().equalsIgnoreCase(policyName)) {
						ps.add(plist.get(i));
					}
				}
			}
			return ps;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieves policy data from Api searching by policy category.
	 * 
	 * @param apiId : String
	 * @param policyCategory : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> getPolicyApiByPolicyCategory(String apiId, String policyCategory){
		Api api = getApiById(apiId);
		try {
			List<Policy> plist = (List<Policy>) api.getPolicy();
			List<Policy> ps = new ArrayList<Policy>();

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getCategory()
							.equalsIgnoreCase(policyCategory)) {
						ps.add(plist.get(i));
					}
				}
			}
			return ps;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieves policy data from Api searching by policy type.
	 * 
	 * @param apiId : String
	 * @param policyType : String
	 * @return list of {@link Policy} instances
	 */
	public List<Policy> getPolicyApiByPolicyType(String apiId, String policyType){
		Api api = getApiById(apiId);
		try {
			List<Policy> plist = (List<Policy>) api.getPolicy();
			List<Policy> ps = new ArrayList<Policy>();

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getType().equalsIgnoreCase(policyType)) {
						ps.add(plist.get(i));
					}
				}
			}
			return ps;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Add app to an Api instance.
	 * First this method checks if name is undefined, otherwise it 
	 * throws IllegalArgumentException.
	 * After that it retrieves api data searching by id and update it, adding
	 * the new app.
	 * 
	 * @param apiId : String
	 * @param app : instance of {@link App}
	 * @return added instance of {@link App}
	 */
	/*public App addAppApi(String apiId, App app){
		if(app.getName()==null){
			throw new IllegalArgumentException("App name is required.");
		}
		if(appApiExists(apiId, app.getName())){
			throw new IllegalArgumentException("App with this name already exists.");
		}
		if(app.getId()==null || app.getId().equalsIgnoreCase("")){
			app.setId(generateId());
		}
		//set app key
		app.setKey(UUID.randomUUID().toString());
		// get api and add app
		Api api = getApiById(apiId);
		List<App> alist = api.getApp();
		if (alist != null) {
			alist.add(app);
		} else {
			List<App> as = new ArrayList<App>();
			as.add(app);
			api.setApp(as);
		}

		// update api
		updateApi(api);
		
		return getAppApiByAppId(apiId, app.getId());
	}
	*/
	/**
	 * Updates an app in Api instance.
	 * First this method checks if name is undefined, otherwise it 
	 * throws IllegalArgumentException.
	 * After that it retrieves api data searching by id and update it, adding
	 * the new app.
	 * Then it retrieves the wanted app by its id and sets fields.
	 * It updates api.
	 * 
	 * @param apiId : String
	 * @param app : instance of {@link App}
	 * @return instance of {@link Ap} with updates
	 */
	/*public App updateAppApi(String apiId, App app){
		if(app.getName()==null){
			throw new IllegalArgumentException("App name is required.");
		}
		//retrieve api searching by id
		Api api = getApiById(apiId);
		//retrieve app
		List<App> alist = api.getApp();
		App oldapp = null;
		for(int i=0;i<alist.size();i++){
			if(alist.get(i).getId().equalsIgnoreCase(app.getId())){
				oldapp = alist.get(i);
			}
		}
		if(oldapp!=null){
			if(!oldapp.getName().equalsIgnoreCase(app.getName())){
				if(appApiExists(apiId, app.getName())){
					throw new IllegalArgumentException("App with this name already exists.");
				}
			}
			oldapp.setName(app.getName());
			//set app key
			oldapp.setKey(UUID.randomUUID().toString());
		}
		// update api
		updateApi(api);
		
		return getAppApiByAppId(apiId, app.getId());
	}*/
	
	/**
	 * Deletes an app from Api.
	 * 
	 * @param apiId : String
	 * @param appId : String
	 */
	/*public void deleteAppApi(String apiId, String appId){
		// retrieves api
		Api api = getApiById(apiId);
		// retrieves app
		List<App> alist = api.getApp();

		for (int i = 0; i < alist.size(); i++) {
			if (alist.get(i).getId().equalsIgnoreCase(appId)) {
				// delete resource
				alist.remove(i);
			}
		}
		api.setApp(alist);
		updateApi(api);
	}*/
	
	/**
	 * Retrieves app from Api searching by app id.
	 * 
	 * @param apiId : String
	 * @param appId : String
	 * @return instance of {@link App}
	 */
	/*public App getAppApiByAppId(String apiId, String appId){
		Api api = getApiById(apiId);
		try {
			List<App> alist = api.getApp();
			App app = null;

			if (alist != null) {
				for (int i = 0; i < alist.size(); i++) {
					if (alist.get(i).getId().equalsIgnoreCase(appId)) {
						app = alist.get(i);
					}
				}
			}
			return app;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}*/
	
	/**
	 * Retrieves apps from Api searching by app name.
	 * 
	 * @param apiId : String
	 * @param appName : String
	 * @return list of {@link App} instances
	 */
	/*public List<App> getAppApiByAppName(String apiId, String appName){
		Api api = getApiById(apiId);
		try {
			List<App> alist = api.getApp();
			List<App> apps = new ArrayList<App>();

			if (alist != null) {
				for (int i = 0; i < alist.size(); i++) {
					if (alist.get(i).getName().equalsIgnoreCase(appName)) {
						apps.add(alist.get(i));
					}
				}
			}
			return apps;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}*/
	
	/**
	 * Retrieves apps from Api searching by app key.
	 * 
	 * @param apiId
	 * @param appKey
	 * @return
	 */
	/*public List<App> getAppApiByAppKey(String apiId, String appKey){
		Api api = getApiById(apiId);
		try {
			List<App> alist = api.getApp();
			List<App> apps = new ArrayList<App>();

			if (alist != null) {
				for (int i = 0; i < alist.size(); i++) {
					if (alist.get(i).getKey().equalsIgnoreCase(appKey)) {
						apps.add(alist.get(i));
					}
				}
			}
			return apps;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}*/
	
	/*
	 * Policy in Resource Api
	 */
	/**
	 * Add a policy resource in an api.
	 * It throws java.lang.IllegalArgumentException if some required parameters are not
	 * added and if a policy with same name already exists.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Policy}
	 * @return updated instance of {@link Resource}
	 */
	public Resource addPolicyResourceApi(String apiId, String resourceId, Policy p){
		// checks policy fields
		if (p.getName() == null || p.getType() == null) {
			throw new IllegalArgumentException(
					"Policy name and type are required.");
		}
		isPolicyInstanceOf(p);
		if (p.getId() == null || p.getId().equalsIgnoreCase("")) {
			p.setId(generateId());
		}
		if (policyResourceApiExists(apiId,resourceId, p.getName())) {
			throw new IllegalArgumentException(
					"Policy with this name already exists.");
		}
		if(p instanceof SpikeArrest || p instanceof Quota){
			p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
		}
		// get resource api and add policy
		Resource r = getResourceApiByResourceId(apiId, resourceId);
		List<Policy> plist = (List<Policy>) r.getPolicy();
		if (plist != null) {
			plist.add(p);
		} else {
			List<Policy> ps = new ArrayList<Policy>();
			ps.add(p);
			r.setPolicy(ps);
		}

		// update resource api
		return updateResourceApi(apiId, r);
	}
	
	/**
	 * Update a policy resource in an api.
	 * It throws java.lang.IllegalArgumentException if some required parameters
	 * are missing.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Policy}
	 * @return updated instance of {@link Resource}
	 */
	public Resource updatePolicyResourceApi(String apiId, String resourceId, Policy p){
		// checks policy fields
		if (p.getName() == null || p.getType() == null) {
			throw new IllegalArgumentException(
					"Policy name and type are required.");
		}
		isPolicyInstanceOf(p);
		// check category
		if (p instanceof SpikeArrest || p instanceof Quota) {
			if (p.getCategory()==null || !p.getCategory().equalsIgnoreCase(
					POLICY_CATEGORY.QualityOfService.toString())) {
				p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
				// throw new
				// IllegalArgumentException("Policy category is wrong.");
			}
		}
		// get resource api and add policy
		Resource r = getResourceApiByResourceId(apiId, resourceId);
		// retrieve policy
		List<Policy> plist = (List<Policy>) r.getPolicy();
		Policy oldp = null;
		for (int i = 0; i < plist.size(); i++) {
			if (plist.get(i).getId().equalsIgnoreCase(p.getId())) {
				oldp = plist.get(i);
				plist.remove(i);
			}
		}
		if (oldp != null) {
			if (!oldp.getName().equalsIgnoreCase(p.getName())) {
				System.out.println("Different name");
				if (policyResourceApiExists(apiId,resourceId, p.getName())) {
					throw new IllegalArgumentException(
							"Policy with this name already exists.");
				}
			}
			plist.add(p);
		}
		// update api
		return updateResourceApi(apiId, r);
	}
	
	/**
	 * Delete a policy resource in an api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return updated instance of {@link Resource}
	 */
	public Resource deletePolicyResourceApi(String apiId, String resourceId, String policyId){
		// retrieves resource
		Resource r = getResourceApiByResourceId(apiId, resourceId);
		// retrieves policy
		List<Policy> plist = (List<Policy>) r.getPolicy();

		if (plist != null) {
			for (int i = 0; i < plist.size(); i++) {
				if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
					// delete resource
					plist.remove(i);
				}
			}
		}
		r.setPolicy(plist);
		return updateResourceApi(apiId, r);
	}
	
	/**
	 * Retrieve policy resource by policyId
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link Policy} resource
	 */
	public Policy getPolicyResourceApiByResourceId(String apiId, String resourceId, String policyId){
		Resource resource = getResourceApiByResourceId(apiId, resourceId);
		try {
			List<Policy> plist = (List<Policy>) resource.getPolicy();
			Policy p = null;

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
						p = plist.get(i);
					}
				}
			}
			return p;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieve spike arrest policy resource by policyId
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link SpikeArrest} resource
	 */
	public Policy getSpikeArrestPolicyResourceApiByResourceId(String apiId, String resourceId, 
			String policyId){
		Resource resource = getResourceApiByResourceId(apiId, resourceId);
		try {
			List<Policy> plist = resource.getPolicy();
			SpikeArrest p = null;

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
						p = (SpikeArrest) plist.get(i);
					}
				}
			}
			return p;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/**
	 * Retrieve quota policy resource by policyId
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link Quota} resource
	 */
	public Policy getQuotaPolicyResourceApiByResourceId(String apiId, String resourceId, String policyId){
		Resource resource = getResourceApiByResourceId(apiId, resourceId);
		try {
			List<Policy> plist = resource.getPolicy();
			Quota p = null;

			if (plist != null) {
				for (int i = 0; i < plist.size(); i++) {
					if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
						p = (Quota) plist.get(i);
					}
				}
			}
			return p;
		} catch (java.lang.NullPointerException n) {
			return null;
		}
	}
	
	/*
	 * API STATUS
	 */
	
	/**
	 * Retrieves list of status of an api.
	 * 
	 * @param apiId : String
	 * @return list of {@link Status} instances
	 */
	public List<Status> getApiStatus(String apiId){
		Api api = getApiById(apiId);
		return api.getStatus();
	}
	
	/**
	 * Retrieves an api status searching by name.
	 * 
	 * @param apiId : String
	 * @param statusName : String 
	 * @return instance of {@link Status}
	 */
	public Status getApiStatusByStatusName(String apiId, String statusName){
		List<Status> slist = getApiStatus(apiId);
		if(slist!=null && slist.size()>0){
			for(int i=0;i<slist.size();i++){
				if(slist.get(i).getName().equals(statusName)){
					return slist.get(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Creates a new status entry for an api.
	 * 
	 * @param apiId : String
	 * @param s : instance of {@link Status}
	 * @return list of {@link Status} instances
	 */
	public List<Status> addStatusApi(String apiId, Status s){
		
		//check status field: name and quota
		if(s.getName() == null || s.getQuota() == 0){
			throw new IllegalArgumentException("Status name and quota are required.");
		}
		
		if(statusNameApiExists(apiId, s.getName())){
			throw new IllegalArgumentException("Status with this name already exists.");
		}
		
		Api api = getApiById(apiId);
		if(api!=null){
			List<Status> slist = api.getStatus();
			
			if(slist != null){
				slist.add(s);
			}else{
				slist = new ArrayList<Status>();
				slist.add(s);
				api.setStatus(slist);
			}
		}else{
			throw new IllegalArgumentException("Api does not exist.");
		}
		// update api
		updateApi(api);
		
		return getApiStatus(apiId);
	}
	
	/**
	 * Updates a status of an api.
	 * 
	 * @param apiId : String
	 * @param s : instance of {@link Status}
	 * @return list of {@link Status} instances
	 */
	public List<Status> updateStatusApi(String apiId, Status s){
		
		// check status field: name and quota
		if (s.getName() == null || s.getQuota() == 0) {
			throw new IllegalArgumentException("Status name and quota are required.");
		}

		/*if(statusNameApiExists(apiId, s.getName())){
			throw new IllegalArgumentException("Status with this name already exists.");
		}*/

		Api api = getApiById(apiId);
		if (api != null) {
			List<Status> slist = api.getStatus();

			if (slist != null) {
				//search status by name
				for(int i=0;i<slist.size();i++){
					if(slist.get(i).getName().equalsIgnoreCase(s.getName())){
						slist.get(i).setQuota(s.getQuota());
					}
				}
				
			} else {
				slist = new ArrayList<Status>();
				slist.add(s);
				api.setStatus(slist);
			}
		} else {
			throw new IllegalArgumentException("Api does not exist.");
		}
		// update api
		updateApi(api);

		return getApiStatus(apiId);
	}
	
	/**
	 * Deletes a status from an api.
	 * 
	 * @param apiId : String
	 * @param s_name : String
	 */
	public void deleteStatusApi(String apiId, String s_name/*Status s*/){
		//TODO
		
		// check status field: name and quota
		if (s_name == null/*s.getName() == null || s.getQuota() == 0*/) {
			throw new IllegalArgumentException("Status name is required.");
		}

		Api api = getApiById(apiId);
		if (api != null) {
			List<Status> slist = api.getStatus();

			if (slist != null) {
				//search status by name
				for(int i=0;i<slist.size();i++){
					if(slist.get(i).getName().equalsIgnoreCase(s_name)){
						slist.remove(i);
					}
				}
			} else {
				throw new IllegalArgumentException("No status api found.");
			}
		} else {
			throw new IllegalArgumentException("Api does not exist.");
		}
		// update api
		updateApi(api);
				
	}
	
	/**
	 * Checks parameter value for policy.
	 * Spike Arrest : name and rate are required.
	 * Quota : name, interval, timeunit and allow count are required.
	 * It throws java.lang.IllegalArgumentException if some required policy 
	 * parameters are missing.
	 * 
	 * @param p : instance of {@link Policy}
	 */
	public void isPolicyInstanceOf(Policy p){
		// check fields of Spike Arrest
		if (p instanceof SpikeArrest) {
			// rate is required
			if (((SpikeArrest) p).getRate() == null) {
				throw new IllegalArgumentException(
						"For policy spike arrest, rate is required.");
			} else {
				// check rate value
				String rate = ((SpikeArrest) p).getRate();
				if (rate.contains("p")) {
					String[] res = rate.split("p");
					try {
						Integer.parseInt(res[0]);
					} catch (NumberFormatException n) {
						throw new IllegalArgumentException(
								"For policy spike arrest, "
										+ "rate value is not in the correct format: <Integer>ps/pm. "
										+ "Ex. 12ps or 12pm.");
					}
					if (!res[1].equalsIgnoreCase("s")
							&& !res[1].equalsIgnoreCase("m")) {
						throw new IllegalArgumentException(
								"For policy spike arrest, "
										+ "rate value is not in the correct format: <Integer>ps/pm. "
										+ "Ex. 12ps or 12pm.");
					}
				} else {
					throw new IllegalArgumentException(
							"For policy spike arrest, "
									+ "rate value is not in the correct format: <Integer>ps/pm. "
									+ "Ex. 12ps or 12pm.");
				}

			}
		}
		// check fields of Quota
		if (p instanceof Quota) {
			//interval, timeunit, allow count are required
			if( ((Quota) p).getInterval()==null || ((Quota) p).getTimeUnit()==null || 
					((Quota) p).getAllowCount()==null ){
				throw new IllegalArgumentException("For policy quota, interval, timeunit and " +
						"allow count are required.");
			}
		}
				
	}
	
	/**
	 * Checks if a policy with a given name is already saved in db.
	 * 
	 * @param apiId : String
	 * @param policyName : String
	 * @return true if a policy with a given name exists, false otherwise
	 */
	public boolean policyApiExists(String apiId, String policyName){
		List<Policy> plist = getPolicyApiByPolicyName(apiId, policyName);
		if(plist!=null && plist.size()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a policy resource with a given name is already saved in db.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyName : String
	 * @return true if a policy resource with a given name exists, false otherwise
	 */
	public boolean policyResourceApiExists(String apiId, String resourceId, String policyName){
		Resource r = getResourceApiByResourceId(apiId, resourceId);

		List<Policy> plist = (List<Policy>) r.getPolicy();
		if (plist != null && plist.size() > 0) {
			for (int i = 0; i < plist.size(); i++) {
				if (plist.get(i).getName().equalsIgnoreCase(policyName)) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Checks if an app with a given name is already saved in db.
	 * 
	 * @param apiId : String
	 * @param appName : String
	 * @return true if an app with a given name exists, false otherwise
	 */
	/*public boolean appApiExists(String apiId, String appName){
		List<App> applist = getAppApiByAppName(apiId, appName);
		if(applist!=null && applist.size()>0){
			return true;
		}
		return false;
	}*/
	
	/**
	 * Checks if a resource with a given name is already saved in db.
	 * 
	 * @param apiId : String
	 * @param resourceName : String
	 * @return true if a resource with a given name exists, false otherwise
	 */
	public boolean resourceApiExists(String apiId, String resourceName){
		List<Resource> rlist = getResourceApiByResourceName(apiId, resourceName);
		if(rlist!=null && rlist.size()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a resource with a given uri is already saved in db.
	 * 
	 * @param apiId : String
	 * @param resourceName : String
	 * @return true if a resource with a given name exists, false otherwise
	 */
	public boolean resourceUriApiExists(String apiId, String resourceUri){
		List<Resource> rlist = getResourceApiByResourceUri(apiId, resourceUri);
		if(rlist!=null && rlist.size()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a status has already a specific name.
	 * 
	 * @param apiId : String
	 * @param statusName : String
	 * @return true if a status with this name exists, otherwise false.
	 */
	public boolean statusNameApiExists(String apiId, String statusName){
		Api api = getApiById(apiId);
		if(api!=null){
			List<Status> slist = api.getStatus();
			if(slist!=null){
				for(int i=0;i<slist.size();i++){
					if(slist.get(i).getName().equalsIgnoreCase(statusName)){
						return true;
					}
				}
			}
		}
		return false;
	}

}
