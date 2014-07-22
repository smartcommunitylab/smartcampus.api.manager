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

import org.apache.commons.validator.routines.UrlValidator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.Constants;
import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.repository.ApiRepository;
import eu.trentorise.smartcampus.api.manager.repository.AppRepository;
import eu.trentorise.smartcampus.api.manager.repository.PolicyRepository;
import eu.trentorise.smartcampus.api.manager.repository.ResourceRepository;

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
		isPolicyInstanceOf(p);
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
	
	/*
	 * Resource
	 */
	
	/**
	 * Retrieves all resources saved in db.
	 * 
	 * @return list of {@link Resource} instances
	 */
	public List<Resource> listResource(){
		return resourcerepository.findAll();
	}
	
	/**
	 * Retrieves resource data searching by id.
	 * 
	 * @param id : String
	 * @return instance of {@link Resource}
	 */
	public Resource getResourceById(String id){
		return (Resource) resourcerepository.findById(id).get(0);
	}
	
	/**
	 * Retrieves resource data searching by name.
	 * 
	 * @param name : String
	 * @return list of {@link Resource} instances
	 */
	public List<Resource> getResourceByName(String name){
		return (List<Resource>) resourcerepository.findByName(name);
	}
	
	/**
	 * Create a new resource and saved it in db.
	 * If name, uri or verb are undefined then it throws IllegalArgumentException,
	 * because they are required.
	 * 
	 * @param r : instance of {@link Resource}
	 * @return saved instance of {@link Resource}
	 */
	public Resource addResource(Resource r){
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
	
	/**
	 * Update a resource in db.
	 * 
	 * @param r : instance of {@link Resource}
	 * @return updated instance of {@link Resource}
	 */
	public Resource updateResource(Resource r){
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
	
	/**
	 * Delete an existing resource in db.
	 * 
	 * @param r : instance of {@link Resource}
	 */
	public void deleteResource(Resource r){
		resourcerepository.delete(r);
	}
	
	
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
	 * @return updated instance of {@link Api}
	 */
	public Api addResourceApi(String apiId, Resource r){
		//check resource fields
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
		return updateApi(api);
		
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
	 * @return updated instance of {@link Api}
	 */
	public Api updateResourceApi(String apiId, Resource r){
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
		
		UrlValidator urlValidator = new UrlValidator();
		if(!urlValidator.isValid(r.getUri())){
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
			oldr.setName(r.getName());
			oldr.setUri(r.getUri());
			oldr.setVerb(r.getVerb());
			oldr.setPolicy(r.getPolicy());
			Date today = new Date();
			oldr.setUpdateTime(today.toString());
		}
		//update api
		return updateApi(api);
	}
	
	/**
	 * Deletes a resource from Api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link Api} without deleted resource.
	 */
	public Api deleteResourceApi(String apiId, String resourceId){
		//retrieves api
		Api api = getApiById(apiId);
		//retrieves resource
		List<Resource> rlist = api.getResource();

		for(int i=0; i<rlist.size();i++){
			if(rlist.get(i).getId().equalsIgnoreCase(resourceId)){
				//delete resource
				rlist.remove(i);
			}
		}

		return updateApi(api);
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
		List<Resource> rlist = api.getResource();
		Resource r = null;
		
		for(int i=0; i<rlist.size();i++){
			if(rlist.get(i).getId().equalsIgnoreCase(resourceId)){
				r = rlist.get(i);
			}
		}
		return r;
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
		List<Resource> rlist = api.getResource();
		List<Resource> rs = new ArrayList<Resource>();
		
		for(int i=0; i<rlist.size();i++){
			if(rlist.get(i).getName().equalsIgnoreCase(resourceName)){
				rs.add(rlist.get(i));
			}
		}
		return rs;
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
	 * @return instance of {@link Api}
	 */
	public Api addPolicyApi(String apiId, Policy p){
		//checks policy fields
		if(p.getName()==null || p.getType()==null){
			throw new IllegalArgumentException("Policy name and type are required.");
		}
		isPolicyInstanceOf(p);
		if(p.getId()==null || p.getId().equalsIgnoreCase("")){
			p.setId(generateId());
		}
		// get api and add policy
		Api api = getApiById(apiId);
		List<Policy> plist = api.getPolicy();
		if (plist != null) {
			plist.add(p);
		} else {
			List<Policy> ps = new ArrayList<Policy>();
			ps.add(p);
			api.setPolicy(ps);
		}

		// update api
		return updateApi(api);
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
	 * @return updated instance of {@link Api}
	 */
	public Api updatePolicyApi(String apiId, Policy p){
		//checks policy fields
		if(p.getName()==null || p.getType()==null){
			throw new IllegalArgumentException("Policy name and type are required.");
		}
		isPolicyInstanceOf(p);
		//retrieve api searching by id
		Api api = getApiById(apiId);
		//retrieve policy
		List<Policy> plist = api.getPolicy();
		Policy oldp = null;
		for(int i=0;i<plist.size();i++){
			if(plist.get(i).getId().equalsIgnoreCase(p.getId())){
				oldp = plist.get(i);
			}
		}
		if(oldp!=null){
			oldp.setName(p.getName());
			oldp.setNotes(p.getNotes());
			oldp.setCategory(p.getCategory());
			oldp.setType(p.getType());
		}
		//update api
		return updateApi(api);
	}
	
	/**
	 * Deletes policy from Api.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link Api} without deleted Policy
	 */
	public Api deletePolicyApi(String apiId, String policyId){
		// retrieves api
		Api api = getApiById(apiId);
		// retrieves policy
		List<Policy> plist = api.getPolicy();

		for (int i = 0; i < plist.size(); i++) {
			if (plist.get(i).getId().equalsIgnoreCase(policyId)) {
				// delete resource
				plist.remove(i);
			}
		}

		return updateApi(api);
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
		List<Policy> plist = api.getPolicy();
		Policy p = null;
		
		for(int i=0; i<plist.size();i++){
			if(plist.get(i).getId().equalsIgnoreCase(policyId)){
				p = plist.get(i);
			}
		}
		return p;
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
		List<Policy> plist = api.getPolicy();
		List<Policy> ps = new ArrayList<Policy>();
		
		for(int i=0; i<plist.size();i++){
			if(plist.get(i).getName().equalsIgnoreCase(policyName)){
				ps.add(plist.get(i));
			}
		}
		return ps;
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
		List<Policy> plist = api.getPolicy();
		List<Policy> ps = new ArrayList<Policy>();
		
		for(int i=0; i<plist.size();i++){
			if(plist.get(i).getCategory().equalsIgnoreCase(policyCategory)){
				ps.add(plist.get(i));
			}
		}
		return ps;
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
		List<Policy> plist = api.getPolicy();
		List<Policy> ps = new ArrayList<Policy>();
		
		for(int i=0; i<plist.size();i++){
			if(plist.get(i).getType().equalsIgnoreCase(policyType)){
				ps.add(plist.get(i));
			}
		}
		return ps;
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
	 * @return instance of {@link Api} with new app data
	 */
	public Api addAppApi(String apiId, App app){
		if(app.getName()==null){
			throw new IllegalArgumentException("App name is required.");
		}
		if(app.getId()==null || app.getId().equalsIgnoreCase("")){
			app.setId(generateId());
		}
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
		return updateApi(api);
	}
	
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
	 * @return instance of {@link Api} with update App
	 */
	public Api updateAppApi(String apiId, App app){
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
			oldapp.setName(app.getName());
			oldapp.setKey(app.getKey());
		}
		// update api
		return updateApi(api);
	}
	
	/**
	 * Deletes an app from Api.
	 * 
	 * @param apiId : String
	 * @param appId : String
	 * @return instance of {@link Api} without deleted app
	 */
	public Api deleteAppApi(String apiId, String appId){
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

		return updateApi(api);
	}
	
	/**
	 * Retrieves app from Api searching by app id.
	 * 
	 * @param apiId : String
	 * @param appId : String
	 * @return instance of {@link App}
	 */
	public App getAppApiByAppId(String apiId, String appId){
		Api api = getApiById(apiId);
		List<App> alist = api.getApp();
		App app = null;
		
		for(int i=0; i<alist.size();i++){
			if(alist.get(i).getId().equalsIgnoreCase(appId)){
				app = alist.get(i);
			}
		}
		return app;
	}
	
	/**
	 * Retrieves apps from Api searching by app name.
	 * 
	 * @param apiId : String
	 * @param appName : String
	 * @return list of {@link App} instances
	 */
	public List<App> getAppApiByAppName(String apiId, String appName){
		Api api = getApiById(apiId);
		List<App> alist = api.getApp();
		List<App> apps = new ArrayList<App>();
		
		for(int i=0; i<alist.size();i++){
			if(alist.get(i).getName().equalsIgnoreCase(appName)){
				apps.add(alist.get(i));
			}
		}
		return apps;
	}
	
	/**
	 * Retrieves apps from Api searching by app key.
	 * 
	 * @param apiId
	 * @param appKey
	 * @return
	 */
	public List<App> getAppApiByAppKey(String apiId, String appKey){
		Api api = getApiById(apiId);
		List<App> alist = api.getApp();
		List<App> apps = new ArrayList<App>();
		
		for(int i=0; i<alist.size();i++){
			if(alist.get(i).getKey().equalsIgnoreCase(appKey)){
				apps.add(alist.get(i));
			}
		}
		return apps;
	}
	
	/**
	 * Check parameter value for policy.
	 * Spike Arrest : name and rate are required.
	 * Quota : name, interval, timeunit and allow count are required.
	 * 
	 * @param p : instance of {@link Policy}
	 */
	public void isPolicyInstanceOf(Policy p){
		// check fields of Spike Arrest
		if (p instanceof SpikeArrest) {
			//name and rate are required
			if(((SpikeArrest) p).getsName()==null || 
			((SpikeArrest) p).getRate()==null ){
				throw new IllegalArgumentException("For policy spike arrest, name and rate are required.");
			}
		}
		// check fields of Quota
		if (p instanceof Quota) {
			//name, interval, timeunit, allow count are required
			if( ((Quota) p).getqName()==null || ((Quota) p).getInterval()==null ||
			((Quota) p).getTimeUnit()==null || ((Quota) p).getAllowCount()==null ){
				throw new IllegalArgumentException("For policy quota, name, interval, timeunit and " +
						"allow count are required.");
			}
		}
				
	}

}
