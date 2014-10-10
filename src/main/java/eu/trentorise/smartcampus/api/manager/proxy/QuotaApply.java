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


import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.smartcampus.api.manager.model.ApiData;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.QuotaStatus;
import eu.trentorise.smartcampus.api.manager.model.proxy.PolicyQuota;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManagerProxy;

/**
 * Class that apply Quota logic.
 * 
 * @author Giulia Canobbio
 *
 */
public class QuotaApply implements PolicyDatastoreApply{
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(QuotaApply.class);
	/**
	 * Instance of {@link PersistenceManagerProxy}.
	 */
	private PersistenceManagerProxy pmanager;
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	private PersistenceManager manager;
	
	//global variables
	private Quota p;
	private String apiId;
	private String resourceId;
	private String appId;
	
	private int interval;
	private String timeUnit;
	
	/**
	 * Constructor 
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param p : instance of {@link Quota}
	 */
	public QuotaApply(String apiId, String resourceId, String appId, Quota p){
		this.p = p;
		this.apiId = apiId;
		this.resourceId = resourceId;
		this.appId = appId;
	}
	
	/**
	 * 
	 * @param pmanager : instance of {@link PersistenceManagerProxy}
	 */
	public void setPManager(PersistenceManagerProxy pmanager){
		this.pmanager = pmanager;
	}
	
	/**
	 * 
	 * @param manager : instance of {@link PersistenceManager}
	 */
	public void setManager(PersistenceManager manager){
		this.manager = manager;
	}

	@Override
	public void apply() {
		
		/*logger.info("Applying quota policy..");
		logger.info("Q - Api id: {}, ",apiId);
		logger.info("Q - Resource id: {}, ",resourceId);
		logger.info("Q - App id: {}, ",appId);
		logger.info("Q - Policy: {}. ",p.getName());*/
		
		this.interval = p.getInterval();
		this.timeUnit = p.getTimeUnit();

		decision(appId, resourceId, apiId);

	}
	
	/**
	 * If status is different from DEFAULT, then find quota.
	 * 
	 * @return quota, int 
	 */
	private int getQuotaValue(){
		int quota = 0;

		// get app apiData
		App app = manager.getAppById(appId);
		List<ApiData> list = null;
		if (app != null) {
			list = app.getApis();
		}
		String appApiStatus = "DEFAULT";

		// if apiId is in list - retrieve status
		if (list != null && list.size() > 0) {
			if (list.contains(apiId)) {
				// retrieve status from app data
				for (int i = 0; i < list.size(); i++) {
					// find apiId
					if (list.get(i).getApiId().equalsIgnoreCase(apiId)) {
						appApiStatus = list.get(i).getApiStatus();
					}
				}
			}
		}

		// from policy status retrieves quota
		List<QuotaStatus> qslist = p.getQstatus();
		if (qslist != null && qslist.size() > 0
				&& !appApiStatus.equalsIgnoreCase("DEFAULT")) {
			for (int i = 0; i < qslist.size(); i++) {
				if (qslist.get(i).getName().equalsIgnoreCase(appApiStatus)) {
					quota = qslist.get(i).getQuota();
				}
			}
		}

		return quota;
	}
	
	/**
	 * Checks if access to an api resource can be granted or not.
	 * It throws a security exception if access is denied.
	 * 
	 * @param appId : String
	 * @param resourceId : String
	 * @param apiId : String
	 */
	private void decision(String appId, String resourceId, String apiId) {

		Date currentTime = new Date();

		boolean decision = false;

		// check input because resource id cannot be null
		if (apiId == null && resourceId == null) {
			throw new IllegalArgumentException(
					"Api or Resource id cannot be null.");
		} else {

			// interval and timeUnit is used for calculating timeLimit
			int t = 0;
			// time unit
			if (timeUnit.equalsIgnoreCase("second")) {
				t = 1;
			} else if (timeUnit.equalsIgnoreCase("minute")) {
				t = 60;
			} else if (timeUnit.equalsIgnoreCase("hour")) {
				t = 3600;
			} else if (timeUnit.equalsIgnoreCase("day")) {
				t = 86400;
			} else if (timeUnit.equalsIgnoreCase("month")) {
				t = 2592000;
			}

			int timeLimit = interval * t * 1000; // in milliseconds
			int resourceQuota = getQuotaValue();

			if (appId == null) {
				decision = QuotaDecision(timeLimit, resourceQuota, apiId,
						resourceId, currentTime);
			} else {
				decision = QuotaDecision(timeLimit, resourceQuota, apiId,
						appId, resourceId, currentTime);
			}
		}

		if (decision)
			logger.info("Quota policy --> GRANT ");
		else{
			logger.info("Quota policy --> DENY ");
			throw new SecurityException("DENY - " +
				" Quota policy DENIES access.");
		}
	}
	
	/**
	 * Checks if an access of app to a api resource can be granted.
	 * 
	 * @param timeLimit : int
	 * @param resourceQuota : int
	 * @param apiId : String
	 * @param appId : String
	 * @param resourceId : String
	 * @param currentTime : Date
	 * @return if access is granted then true else false
	 */
	private boolean QuotaDecision(int timeLimit, int resourceQuota,
			String apiId, String appId, String resourceId, Date currentTime) {

		PolicyQuota pq = pmanager.retrievePolicyQuotaByParamIds(apiId,
				resourceId, appId);

		if (pq==null) {
			updatePolicyQuota(pq, apiId, resourceId, appId, currentTime,
					timeLimit);
			return true;
		} else {

			updatePolicyQuota(pq, apiId, resourceId, appId,
					currentTime, timeLimit);

			boolean decision = false;
			
			//global is true
			int count = pq.getCount();
			if(p.isGlobal()){
				count = pmanager.sumGlobalQuota(apiId, resourceId);
			}
			
			if (resourceQuota!=0 && count <= resourceQuota) {
				decision = true;
			}
			
			if (resourceQuota==0 && count <= p.getAllowCount()) {
				decision = true;
			}

			return decision;

		}

	}
	
	/**
	 * Checks if anonymous access to api resource can be granted.
	 * 
	 * @param timeLimit : int
	 * @param resourceQuota : int
	 * @param apiId : String
	 * @param resourceId : String
	 * @param currentTime : Date
	 * @return if access is granted then true else false
	 */
	private boolean QuotaDecision(int timeLimit, int resourceQuota, 
			String apiId, String resourceId, Date currentTime) {

		PolicyQuota pq = pmanager.retrievePolicyQuotaByParamIds(apiId, resourceId, null);
		
		int counter = 1;

		if (pq!=null && DatesDiff(pq.getTime(), currentTime) < timeLimit) {
			//global is true
			if(p.isGlobal()){
				counter = +pmanager.sumGlobalQuota(apiId, resourceId);
			}else{
				counter = +pq.getCount();
			}
		}
		
		updatePolicyQuota(pq, apiId, resourceId, null, currentTime,
				timeLimit);

		if (resourceQuota!=0 && counter <= resourceQuota)
			return true;
		else if(resourceQuota==0 && counter <= p.getAllowCount())
			return true;
		else
			return false;

	}

	/**
	 * Calculates difference from d2 to d1 in milliseconds.
	 * 
	 * @param d1 : Date
	 * @param d2 : Date
	 * @return long value
	 */
	private long DatesDiff(Date d1, Date d2) {
		long millisDiff = d2.getTime() - d1.getTime();
		return millisDiff;
	}

	/**
	 * Update table policy quota.
	 * 
	 * @param q : instance of {@link PolicyQuota}
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param currentTime : Date
	 * @param timeLimit : int
	 */
	private void updatePolicyQuota(PolicyQuota q, String apiId,
			String resourceId, String appId, Date currentTime, int timeLimit) {
		if (q == null) {
			q = new PolicyQuota();
			q.setApiId(apiId);
			q.setResourceId(resourceId);
			q.setAppId(appId);
			q.setTime(currentTime);
			q.setCount(1);
			
			//for callback
			q.setState("init");
			q.setPrevTime(currentTime);
			
			pmanager.findAndModify(q, true);
		} else {
			if (DatesDiff(q.getTime(), currentTime) < timeLimit) {
				int quotaCount = q.getCount() + 1;
				q.setCount(quotaCount);
				
				//for callback
				q.setState("pending");
				q.setPrevTime(q.getTime());
				
				pmanager.findAndModify(q, true);
			} else {
				//for callback
				q.setPrevTime(q.getTime());
				q.setState("pending");
				
				//current time
				q.setTime(currentTime);
				q.setCount(1);
				pmanager.findAndModify(q,false);
			}
		}
	}
	
	
}
