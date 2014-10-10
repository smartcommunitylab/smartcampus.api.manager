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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.proxy.LastTime;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManagerProxy;

/**
 * Class that apply Spike Arrest logic.
 * 
 * @author Giulia Canobbio.
 *
 */
public class SpikeArrestApply implements PolicyDatastoreApply{
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(SpikeArrestApply.class);
	/**
	 * Instance of {@link PersistenceManagerProxy}.
	 */
	private PersistenceManagerProxy pmanager;
	
	//Global variable
	private SpikeArrest sp;
	private String apiId;
	private String resourceId;
	private String appId;
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param sp : instance of {@link SpikeArrest}
	 */
	public SpikeArrestApply(String apiId, String resourceId, String appId, SpikeArrest sp){
		this.sp = sp;
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

	@Override
	public void apply() {
		logger.info("Applying spike arrest policy..");
		logger.info("SP - Api id: {}, ",apiId);
		logger.info("SP - Resource id: {}, ",resourceId);
		logger.info("SP - App id: {}, ",appId);
		logger.info("SP - Policy: {}. ",sp.getName());
		decision();
	}
	
	/**
	 * Function that decide if an access to resource or api can be granted or not 
	 * by applying spike arrest policy.
	 * It throws a security exception if access is denied.
	 */
	private void decision() {
		// Retrieve rate from policy spike arrest
		String rate = sp.getRate();

		Date currentTime = new Date();

		boolean decision;
		// check that resource id cannot be null
		if (apiId == null && resourceId == null) {
			throw new IllegalArgumentException(
					"Api or Resource id cannot be null.");
		} else {
			
			if (appId == null) {
				decision = SpikeArrestDecision(rate, apiId, resourceId,
						currentTime);

			} else {
				decision = SpikeArrestDecision(rate, apiId, appId, resourceId,
						currentTime);
			}

		}

		if (decision)
			logger.info("Spike Arrest policy --> GRANT ");
		else{
			logger.info("Spike Arrest policy --> DENY ");
			throw new SecurityException("DENY - " +
				" Spike Arrest policy DENIES access.");
		}
	}
	
	/**
	 * Function that checks last anonymous access to an api resource.
	 * It retrieves data from last time table where app id is null.
	 * It checks the max value of date and check if the different of two dates is greater than
	 * rate.
	 * If access is granted then it return true otherwise false.
	 * 
	 * @param rate : String
	 * @param apiId : String
	 * @param resourceId : String
	 * @param currentTime : Date
	 * @return if access is granted then true else false
	 */
	private  boolean SpikeArrestDecision(String rate, String apiId, String resourceId, Date currentTime) {	
		List<LastTime> list = new ArrayList<LastTime>();
		
		LastTime lastTimeApp= pmanager.retrievePolicySpikeArrestByApiAndRAndAppId(apiId, resourceId, null); 
		LastTime ltime = pmanager.retrievePolicySpikeArrestByApiAndResouceId(apiId, resourceId);
		
		if(ltime!=null){
			list.add(ltime);
		}
		if(lastTimeApp!=null){
			list.add(lastTimeApp);
		}
		
		if(list.size()>0){
			Date maxLastTime;
			if(sp.isGlobal()){
				maxLastTime = pmanager.dateGlobalSpikeArrest(apiId, resourceId);
			}else{
				maxLastTime= getMax(list);
			}
			int t=intervalTimeValue(rate);
		
		
			if(	DatesDiff(maxLastTime,currentTime) >t){
				updateSpikeArrestApply(lastTimeApp, apiId, resourceId, null, currentTime);
				return true;  
			}else
				return false;
		}else{
			updateSpikeArrestApply(null, apiId, resourceId, null, currentTime);
			return true;
		}
		
	}	
	
	/**
	 * Function that checks last access of an app to an api resource.
	 * It retrieves data from last time table.
	 * It checks if the different of two dates is greater than rate.
	 * If access is granted then it return true otherwise false.
	 * 
	 * @param rate : String
	 * @param apiId : String
	 * @param appId : String 
	 * @param resourceId : String
	 * @param currentTime : Date
	 * @return if access is granted then true else false
	 */
	private boolean SpikeArrestDecision(String rate, String apiId, String appId, String resourceId, 
			Date currentTime) {	
		  
		int t= intervalTimeValue(rate);
		
		LastTime lastTime= pmanager.retrievePolicySpikeArrestByApiAndRAndAppId(apiId, resourceId, appId); 
		
		Date timeCheck = lastTime.getTime();
		if(sp.isGlobal()){
			timeCheck = pmanager.dateGlobalSpikeArrest(apiId, resourceId);
		}
	
		if(lastTime==null || DatesDiff(timeCheck,currentTime)>t){
			updateSpikeArrestApply(lastTime, apiId, resourceId, appId, currentTime);
			return true;
		}
		else return false;
				   	    	    	 
	}
	
	/**
	 * Return max value of last time dates.
	 * 
	 * @param lastTimes : list of {@link LastTime} instances
	 * @return maximum value
	 */
	private Date getMax(List<LastTime> lastTimes) {
		Date max = new Date(Long.MIN_VALUE);
		for (int i = 0; i < lastTimes.size(); i++) {
			Date lastTime = lastTimes.get(i).getTime();
			if (lastTime.after(max)) {
				max = lastTime;
			}
		}
		return max;
	}

	/**
	 * Calculates the difference of two date in milliseconds.
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
	 * Calculates rate in milliseconds.
	 * 
	 * @param rate : String
	 * @return rate in milliseconds
	 */
	private int intervalTimeValue(String rate) {
		int t;
		int rateLength = rate.length();
		char timeUnit = rate.charAt(rateLength - 1);
		String timeValue = rate.replaceAll("[^0-9]", "");

		if (timeUnit == 'm') {
			t = 60 / Integer.parseInt(timeValue); // result is in seconds
			t = t * 1000; //milliseconds
		} else {
			t = 1000 / Integer.parseInt(timeValue);
		} // result is in milliseconds
		return t;
	}
	
	/**
	 * Updates table last time in db.
	 * 
	 * @param lastTime : instance of {@link LastTime}
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @param currentTime : Date
	 */
	private void updateSpikeArrestApply(LastTime lastTime, String apiId,
			String resourceId, String appId, Date currentTime) {
		if (lastTime==null) {
			lastTime = new LastTime();
			lastTime.setApiId(apiId);
			lastTime.setResourceId(resourceId);
			lastTime.setAppId(appId);
			lastTime.setTime(currentTime);
			//TODO
			//init - time, for callback
			lastTime.setState("init");
			lastTime.setPrevTime(currentTime);
			pmanager.findAndModify(lastTime);
		} else {
			//init - time, for callback
			lastTime.setState("pending");
			lastTime.setPrevTime(lastTime.getTime());
			//current time
			lastTime.setTime(currentTime);
			pmanager.findAndModify(lastTime);
		}
		

	}

}
