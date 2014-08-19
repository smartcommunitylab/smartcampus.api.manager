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
package eu.trentorise.smartcampus.api.manager.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.ApiData;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.Status;
import eu.trentorise.smartcampus.api.manager.model.proxy.AccessApi;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManagerProxy;

/**
 * Class that retrieves policies of api and its resources.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
public class PolicyDecisionPoint {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PolicyDecisionPoint.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager manager;
	/**
	 * Instance of {@link PersistenceManagerProxy}.
	 */
	@Autowired
	private PersistenceManagerProxy proxyManager;
	/*
	 * Global variables
	 */
	private String apiId;
	private String resourceId;
	private String appId;
	private Map<String, String> headers;
	
	/**
	 * Query {@link RequestHandlerObject} object and retrieve data, such as
	 * ApiID, Resources IDs and Request Headers.
	 * 
	 * @param map : HashMap
	 */
	private void getData(RequestHandlerObject object){
		apiId = object.getApiId();
		resourceId = object.getResourceId();
		appId = object.getAppId();
		headers = object.getHeaders();
		
	}
	
	//TODO check headers map
	
	/**
	 * Retrieves api policies and resource policies.
	 * 
	 * @return list of instance {@link Policy}
	 */
	private List<Policy> policiesList(){
		logger.info("policiesList() - ApiId: {}", apiId);
		
		List<Policy> pToApply = new ArrayList<Policy>();
		//api policies
		Api api =  manager.getApiById(apiId);
		
		//resource policies
		if (resourceId != null) {
			logger.info("policiesList() - ResourceId: {}", resourceId);
			Resource r = manager.getResourceApiByResourceId(apiId,resourceId);
			//retrieve policy resource
			List<Policy> rplist = r.getPolicy();
			if (rplist != null && rplist.size() > 0) {
				pToApply.addAll(rplist);
			}

		}
		// api policies
		try {
			List<Policy> policies = api.getPolicy();
			if (policies != null && policies.size() > 0) {
				pToApply.addAll(policies);
			}
		} catch (NullPointerException n) {
			logger.info("policiesList() - No policies for this api {}", apiId);
		}
		
		return pToApply;
	}
	
	/**
	 * Apply policy logic to the request.
	 * 
	 * @param map : HashMap
	 */
	public void applyPoliciesBatch(RequestHandlerObject obj){
		logger.info("applyPoliciesBatch() - Apply policies....");
		getData(obj);
		
		//boolean decision =  decisionApiAppStatus();
		
		//if(decision){
		List<Policy> pToApply = policiesList();
		
		PolicyDatastoreBatch batch = new PolicyDatastoreBatch();
		
		if (pToApply != null && pToApply.size() > 0) {
			for (int i = 0; i < pToApply.size(); i++) {
				logger.info("applyPoliciesBatch - Apply for each");
				if(pToApply.get(i) instanceof Quota){
					QuotaApply qa = new QuotaApply();
					qa.setQuotaApply(apiId, resourceId, appId,(Quota)pToApply.get(i));
					qa.setPManager(proxyManager);
					qa.setManager(manager);
					batch.add(qa);
				}
				else if(pToApply.get(i) instanceof SpikeArrest){
					batch.add(new SpikeArrestApply());
				}
			}
			batch.apply();
		} else {
			logger.info("applyPoliciesBatch - No policies to apply");
		}
		//batch.clean();
		//}
	}
	
	/*private boolean decisionApiAppStatus(){
		//get Status list
		Api api = manager.getApiById(apiId);
		List<Status> status = api.getStatus();
		
		//get app apiData
		App app = manager.getAppById(appId);
		List<ApiData> list = app.getApis();
		String appApiStatus = "DEFAULT";
		int quota = 0;
		
		//if apiId is in list - retrieve status
		if(list!=null && list.size()>0){
			if(list.contains(apiId)){
				//retrieve status from app data
				for(int i=0;i<list.size();i++){
					//find apiId
					if(list.get(i).getApiId().equalsIgnoreCase(apiId)){
						appApiStatus = list.get(i).getApiStatus();
					}
				}
			}
		}
			
		// from api status list retrieves quota
		if (status != null && status.size() > 0) {
			// retrieves quota
			for (int i = 0; i < status.size(); i++) {
				if (status.get(i).getName().equalsIgnoreCase(appApiStatus)) {
					quota = status.get(i).getQuota();
				}
			}
		}
			
		if (quota != 0) {
			Date today = new Date();
			// retrieve AccessApi data
			AccessApi accessApi = proxyManager.retrieveAccessApiByIdParams(apiId,
					appId);
			if (accessApi != null) {
				Date savedDate = accessApi.getTime();
				// check Date - quota is per day
				if (today.getTime() - savedDate.getTime() >= 86400) {
					// start counter from zero : 1 - update
					accessApi.setCount(1);
					accessApi.setTime(today);
					proxyManager.updateAccessApi(accessApi);
					// GRANT
					logger.info("Access api --> GRANT ");
					return true;
				} else {
					// check counter
					int savedCounter = accessApi.getCount();
					if (savedCounter < quota) {
						// update
						accessApi.setCount(savedCounter + 1);
						accessApi.setTime(today);
						proxyManager.updateAccessApi(accessApi);
						// GRANT
						logger.info("Access api --> GRANT ");
						return true;
					}
				}
			} else {
				// update
				accessApi = new AccessApi();
				accessApi.setApiId(apiId);
				accessApi.setAppId(appId);
				accessApi.setCount(1);
				accessApi.setTime(today);
				proxyManager.updateAccessApi(accessApi);
				logger.info("Access api --> GRANT ");
				return true;
			}

		}
		logger.info("Access api --> DENY ");
		return false;
	}*/
}
