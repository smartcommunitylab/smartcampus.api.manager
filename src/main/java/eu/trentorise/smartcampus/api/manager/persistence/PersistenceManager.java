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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.validator.routines.UrlValidator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.Constants;
import eu.trentorise.smartcampus.api.manager.Constants.POLICY_CATEGORY;
import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.ApiData;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.OAuth;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SourceAddress;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.Status;
import eu.trentorise.smartcampus.api.manager.repository.ApiRepository;
import eu.trentorise.smartcampus.api.manager.repository.AppRepository;
import eu.trentorise.smartcampus.api.manager.repository.PolicyRepository;
import eu.trentorise.smartcampus.api.manager.repository.ResourceRepository;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;
import eu.trentorise.smartcampus.api.manager.util.IPAddressValidator;
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
			Api fapi = api.get(0);
			// order
			List<Resource> rlist = fapi.getResource();
			if (rlist != null && rlist.size() > 0) {
				Collections.sort(rlist, new Comparator<Resource>() {

					@Override
					public int compare(Resource o1, Resource o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
			}
			
			List<Policy> plist = fapi.getPolicy();
			if (plist != null && plist.size() > 0) {
				Collections.sort(plist, new Comparator<Policy>() {

					@Override
					public int compare(Policy o1, Policy o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
			}
			
			
			return fapi;
			
		}
		return null;
	}
	
	/**
	 * Retrieves Api data searching by name.
	 * 
	 * @param name : String
	 * @return instance of {@link Api}
	 */
	public Api getApiByName(String name){
		List<Api> api = apirepository.findByName(name);
		if(api!=null && api.size()>0){
			return api.get(0);
		}
		return null;
	}
	
	/**
	 * Retrieves Api data searching by base path.
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
		
		if(api.getName()==null || api.getBasePath()==null){
			throw new IllegalArgumentException("Api name and base path are required.");
		}
		if(api.getId()==null || api.getId().equalsIgnoreCase("")){
			api.setId(generateId());
		}
		if(api.getOwnerId()==null){
			throw new IllegalArgumentException("Cannot save new api. Problem in retrieving" +
					" owner id from context.");
		}
		//basepath pattern
		UriValidation uriValidator = new UriValidation();
		if(!uriValidator.validate(api.getBasePath())){
			throw new IllegalArgumentException("Basepath is not valid. Ex: /sample/v1");
		}
		
		//check name
		Api savedApiName = getApiByName(api.getName());
		
		if(savedApiName!=null){
			throw new IllegalArgumentException("This name is already saved. " +
					"For adding an api, change it");
		}
		//check api basepath
		List<Api> savedApi = getApiByBasePath(api.getBasePath());
		if(savedApi!=null && savedApi.size()>0){
			throw new IllegalArgumentException("This base path is already saved. " +
					"For adding an api, change it");
		}
		//Status DEFAULT
		/*Status s = new Status();
		s.setName("DEFAULT");
		s.setQuota(10);
		List<Status> list = new ArrayList<Status>();
		list.add(s);
		api.setStatus(list);*/
		//date
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
	public List<App> listApp(String user){
		List<App> applist = apprepository.findByOwnerId(user);
		// order
		Collections.sort(applist, new Comparator<App>() {

			@Override
			public int compare(App o1, App o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return applist;
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
	 * Update an key of existing App.
	 * 
	 * @param app : instance of {@link App}
	 * @return updated instace of {@link App}
	 */
	public App updateApp(App app){
		String name = app.getName();
		//Different name or api list
		if(app.getId()==null){
			throw new IllegalArgumentException("App id is required.");
		}
		if(name==null){
			throw new IllegalArgumentException("App name is required.");
		}
		//Retrieve old api
		App oldApp = getAppById(app.getId());
		
		//set app key
		oldApp.setKey(UUID.randomUUID().toString());
		
		return apprepository.save(oldApp);
	}
	
	/**
	 * Deletes an App from db.
	 * 
	 * @param appId : String
	 */
	public void deleteApp(String appId){
		apprepository.delete(appId);
	}
	
	/**
	 * Update api data in an app.
	 * 
	 * @param app : instance of {@link App}
	 * @return updated instance of {@link App}
	 */
	public App updateAppApiData(App app){
		App savedApp = getAppById(app.getId());
		savedApp.setApis(app.getApis());
		return apprepository.save(savedApp);
	}
	
	/**
	 * Deletes api data from app.
	 * 
	 * @param appId : String
	 * @param apiId : String
	 */
	public void deleteAppApiData(String appId, String apiId){
		App app = getAppById(appId);
		if(app!=null){
			List<ApiData> adlist = app.getApis();
			if(adlist!=null && adlist.size()>0){
				for(int i=0; i<adlist.size();i++){
					if(adlist.get(i).getApiId().equalsIgnoreCase(apiId)){
						adlist.remove(i);
					}
				}
			}
		}
		
		updateApp(app);
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
		
		UriValidation uriValidator = new UriValidation();
		if(!uriValidator.validate(r.getUri())){
			throw new IllegalArgumentException("Uri is not valid.");
		}
		
		Date today = new Date();
		r.setCreationTime(today.toString());
		
		//get api and add resource
		Api api;
		api = getApiById(apiId);
		
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
				
				if (r != null) {
					List<Policy> plist = r.getPolicy();
					if (plist != null && plist.size() > 0) {
						Collections.sort(plist, new Comparator<Policy>() {

							@Override
							public int compare(Policy o1, Policy o2) {
								return o1.getName().compareTo(o2.getName());
							}
						});
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
		
		//category
		if(p instanceof SpikeArrest || p instanceof Quota){
			p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
		}
		else{
			p.setCategory(POLICY_CATEGORY.Security.toString());
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
		
		//check category
		if(p instanceof SpikeArrest || p instanceof Quota){
			if(!p.getCategory().equalsIgnoreCase(POLICY_CATEGORY.QualityOfService.toString())){
				p.setCategory(POLICY_CATEGORY.QualityOfService.toString());
			}
		}else{
			if(!p.getCategory().equalsIgnoreCase(POLICY_CATEGORY.Security.toString())){
				p.setCategory(POLICY_CATEGORY.Security.toString());
			}
		}
		
		//retrieve api searching by id
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
		}else{
			p.setCategory(POLICY_CATEGORY.Security.toString());
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
		}else{
			if (p.getCategory()==null || !p.getCategory().equalsIgnoreCase(
					POLICY_CATEGORY.Security.toString())) {
				p.setCategory(POLICY_CATEGORY.Security.toString());
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
	 * Retrieve policy resource by policyId.
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
		if(s.getName() == null /*|| s.getQuota() == 0*/){
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
		if (s.getName() == null /*|| s.getQuota() == 0*/) {
			throw new IllegalArgumentException("Status name and quota are required.");
		}

		Api api = getApiById(apiId);
		if (api != null) {
			List<Status> slist = api.getStatus();

			if (slist != null) {
				//search status by name
				for(int i=0;i<slist.size();i++){
					if(!slist.get(i).getName().equalsIgnoreCase(s.getName())){
						slist.get(i).setName(s.getName());//Quota(s.getQuota());
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
	public void deleteStatusApi(String apiId, String s_name) {
		// check status field: name and quota
		if (s_name == null) {
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
		//check fields of IP Access Control
		if(p instanceof IPAccessControl){
			if( ((IPAccessControl)p).getRule()==null){
				throw new IllegalArgumentException("For policy ip access control, rule " +
						"is required.");
			}
			else{
				if(!((IPAccessControl)p).getRule().equalsIgnoreCase(Constants.POLICY_IP_RULE.ALLOW.toString())
						&&
				!((IPAccessControl)p).getRule().equalsIgnoreCase(Constants.POLICY_IP_RULE.DENY.toString())
				){
					throw new IllegalArgumentException("For policy ip access control, rule " +
							"value can be only ALLOW or DENY.");
				}
			}
			
			//validator of ip address
			IPAddressValidator ipValidator = new IPAddressValidator();
			//whitelist ip
			List<SourceAddress> wlist = ((IPAccessControl)p).getWhiteList();
			if(wlist!=null && wlist.size()>0){
				for (int i = 0; i < wlist.size(); i++) {
					String ip = wlist.get(i).getIp();
					int mask = wlist.get(i).getMask();
					// check mask
					if (mask != 32 && mask != 24 && mask != 16 && mask != 8
							&& mask != 0) {
						throw new IllegalArgumentException(
								"In whitelist, mask possible value are: "
										+ "0, 8, 16, 24, 32.");
					}
					if (!ipValidator.validate(ip)) {
						throw new IllegalArgumentException("In whitelist, Ip "
								+ ip + " with mask " + wlist.get(i).getMask()
								+ " is not valid.");
					}
				}
			}
			//blacklist ip
			List<SourceAddress> blist = ((IPAccessControl)p).getBlackList();
			if(blist!=null && blist.size()>0){
				for (int i = 0; i < blist.size(); i++) {
					String ip = blist.get(i).getIp();
					int mask = blist.get(i).getMask();
					// check mask
					if (mask != 32 && mask != 24 && mask != 16 && mask != 8
							&& mask != 0) {
						throw new IllegalArgumentException(
								"In blacklist, mask possible value are: "
										+ "0, 8, 16, 24, 32.");
					}
					if (!ipValidator.validate(ip)) {
						throw new IllegalArgumentException("In blacklist, Ip "
								+ ip + " with mask " + blist.get(i).getMask()
								+ " is not valid.");
					}
				}
			}
			
			//TODO check ip address
			
			//check subset
			if(((IPAccessControl)p).getRule().equalsIgnoreCase(Constants.POLICY_IP_RULE.ALLOW.toString())
					&& wlist!=null){
				//check that wlist is a subset of blist
				for(int i=0;i<wlist.size();i++){
					boolean isInRange = false;
					try {
						Inet4Address a = (Inet4Address) InetAddress.getByName(wlist.get(i).getIp());
						byte[] b = a.getAddress();
						int subnet = ((b[0] & 0xFF) << 24) |
				                 	((b[1] & 0xFF) << 16) |
				                 	((b[2] & 0xFF) << 8)  |
				                 	((b[3] & 0xFF) << 0);
						int bits = wlist.get(i).getMask();
						
						//check if in blacklist, this addr is in range of one of them
						for(int j=0;j<blist.size();j++){
							Inet4Address ab = (Inet4Address) InetAddress.getByName(blist.get(j).getIp());
							byte[] abb = ab.getAddress();
							int ip = ((b[0] & 0xFF) << 24) |
					                 	((abb[1] & 0xFF) << 16) |
					                 	((abb[2] & 0xFF) << 8)  |
					                 	((abb[3] & 0xFF) << 0);
							
							//check range
							int mask = -1 << (32 - bits);
							
							if((subnet & mask) == (ip & mask)){
								//ip is in range
								isInRange = true;
							}
						}
						
						//If ip in wlist is not in range then error
						if(!isInRange){
							throw new IllegalArgumentException("In list, Ip "
									+ wlist.get(i).getIp() + " with mask " + wlist.get(i).getMask()
									+ " is not an exception of blacklist.");
						}
						
					} catch (UnknownHostException e) {
						throw new IllegalArgumentException("In list, Ip "
								+ wlist.get(i).getIp() + " with mask " + wlist.get(i).getMask()
								+ " is not valid.");
					}
				}
			}
			
			if(((IPAccessControl)p).getRule().equalsIgnoreCase(Constants.POLICY_IP_RULE.DENY.toString())
					&& blist!=null){
				//check that blist is a subset of wlist
				for(int i=0;i<blist.size();i++){
					boolean isInRange = false;
					try {
						Inet4Address a = (Inet4Address) InetAddress.getByName(blist.get(i).getIp());
						byte[] b = a.getAddress();
						int subnet = ((b[0] & 0xFF) << 24) |
				                 	((b[1] & 0xFF) << 16) |
				                 	((b[2] & 0xFF) << 8)  |
				                 	((b[3] & 0xFF) << 0);
						int bits = blist.get(i).getMask();
						
						//check if in whitelist, this addr is in range of one of them
						for(int j=0;j<wlist.size();j++){
							Inet4Address ab = (Inet4Address) InetAddress.getByName(wlist.get(j).getIp());
							byte[] abb = ab.getAddress();
							int ip = ((b[0] & 0xFF) << 24) |
					                 	((abb[1] & 0xFF) << 16) |
					                 	((abb[2] & 0xFF) << 8)  |
					                 	((abb[3] & 0xFF) << 0);
							
							//check range
							int mask = -1 << (32 - bits);
							
							if((subnet & mask) == (ip & mask)){
								//ip is in range
								isInRange = true;
							}
						}
						
						//If ip in wlist is not in range then error
						if(!isInRange){
							throw new IllegalArgumentException("In list, Ip "
									+ blist.get(i).getIp() + " with mask " + blist.get(i).getMask()
									+ " is not an exception of whitelist.");
						}
						
					} catch (UnknownHostException e) {
						throw new IllegalArgumentException("In list, Ip "
								+ wlist.get(i).getIp() + " with mask " + wlist.get(i).getMask()
								+ " is not valid.");
					}
				}
			}
			
			
		}
		
		//check OAuth parameters
		if(p instanceof OAuth){
			//TODO
			/*
			 * operation cannot be null
			 * operation value are only two else error
			 * if operation==Validate Token the
			 * 		validate endpoint cannot be null.
			 * if endpoint is not null then uri validator
			 */
			String oauthOp = ((OAuth) p).getOp();
			if(oauthOp==null){
				throw new IllegalArgumentException("For policy oauth, operation is required.");
			}else{
				if(!oauthOp.equalsIgnoreCase("verifyToken") &&
						!oauthOp.equalsIgnoreCase("validateToken")){
					throw new IllegalArgumentException("For policy oauth, operation possible value" +
							" are: verify token or validate token.");
				}
				
				//check endpoint
				if(oauthOp.equalsIgnoreCase("validateToken")){
					String endp = ((OAuth) p).getEndpoint();
					if(endp==null){
						throw new IllegalArgumentException("For policy oauth, if operation is" +
								" validate token, then enpoint cannot be null.");
					}else{
						UrlValidator validator = new UrlValidator();
						if(!validator.isValid(endp)){
							throw new IllegalArgumentException("Endpoint is not a valid url.");
						}
					}
				}
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
	 * @throws CustomAuthenticationException 
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
	 * @throws CustomAuthenticationException 
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
