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
package eu.trentorise.smartcampus.api.manager.googleAnalytics;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.trentorise.smartcampus.api.manager.googleAnalytics.GoogleAnalyticsConstant.*;

/**
 * Collect data of api access and write them in
 * google analytics account of api owner if 
 * he/she gave us tracking id.
 *
 * The request for Event Tracking is the following:
 * POST /collect HTTP/1.1
 * Host: www.google-analytics.com
 * payload_data
 * 
 * NOTE: for payload_data see class {@link GoogleAnalyticsConstant}
 * 
 * @author Giulia Canobbio
 *
 */
public class GoogleAnalyticsPostCollect {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(GoogleAnalyticsPostCollect.class);
	
	/*
	 * Global variables
	 */
	private static final String ENDPOINT = "http://www.google-analytics.com/collect";
	
	private final String gaClientID = "555";//Anonymous client id
	
	private static String gaTrackingID;
	private String appVersion;
	private String appName;
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param trackingID : String
	 * @param appName : String
	 * @param appVersion : String
	 */
	public GoogleAnalyticsPostCollect(String trackingID, String appName, String appVersion){
		GoogleAnalyticsPostCollect.gaTrackingID = trackingID;
		this.appName = appName;
		this.appVersion = appVersion;
	}
	
	/**
	 * 
	 * @param gaTrackingID : static string
	 */
	public static void setGaTrackingID(String gaTrackingID) {
		GoogleAnalyticsPostCollect.gaTrackingID = gaTrackingID;
	}
	
	/**
	 * 
	 * @param appName : String
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	/**
	 * 
	 * @param appVersion : String
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	/**
	 * Executes post request to collect data on Event Tracking
	 * in owner google analytics account.
	 * 
	 * @param category : String
	 * @param action : String
	 * @param name : String, suppose to be id or name of api
	 * @param value : String, number of access
	 * @return boolean value, if request is ok then true else false.
	 */
	public boolean eventTracking(String category, String action, String name, String value) {
		//check category and action
		if(category==null || action==null){
			logger.error("In Event Tracking, Category and Action are required.");
			return false;
		}
		//set request
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(ENDPOINT);
		method.addParameter(PROTOCAL_VERSION, "1");
		method.addParameter(TRACKING_ID, gaTrackingID);
		method.addParameter(CLIENT_ID, gaClientID);
		method.addParameter(HIT_TYPE, "event");
		method.addParameter(APPLICATION_NAME, appName);
		method.addParameter(APPLICATION_VERSION,
				(!(this.appVersion == null || this.appVersion.length() == 0)) ? this.appVersion
						: DEFAULT_VERSION);
		method.addParameter(EVENT_CATEGORY, category);
		method.addParameter(EVENT_ACTION, action);
		method.addParameter(EVENT_LABEL, name);//name/id of api
		method.addParameter(EVENT_VALUE, value);//1
		try {
			// Checks that value is a valid number.
			Integer.parseInt(value);
			//execute request
			int returnCode = client.executeMethod(method);
			return (returnCode == HttpStatus.SC_OK);
			
		} catch (NumberFormatException e) {
			throw new RuntimeException("Only valid number is allowed", e);
			
		} catch (Exception e) {
			logger.error("ERROR: {}", e.getMessage());
			return false;
			
		}
	}
	
	/**
	 * Executes post request to collect data on Page Tracking
	 * in owner google analytics account.
	 * 
	 * @param hostname : String
	 * @param page : String
	 * @param title : String
	 * @return boolean value, if request is ok then true else false.
	 */
	public boolean pageTracking(String hostname, String page, String title) {
		//check input parameter
		if(hostname==null || page==null || title==null){
			logger.error("In Page Tracking, Hostname, Page and Title are required.");
			return false;
		}
		// set request
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(ENDPOINT);
		method.addParameter(PROTOCAL_VERSION, "1");
		method.addParameter(TRACKING_ID, gaTrackingID);
		method.addParameter(CLIENT_ID, gaClientID);
		method.addParameter(HIT_TYPE, "pageview");
		method.addParameter(APPLICATION_NAME, appName);
		method.addParameter(
				APPLICATION_VERSION,
				(!(this.appVersion == null || this.appVersion.length() == 0)) ? this.appVersion
						: DEFAULT_VERSION);
		method.addParameter(DOC_HOSTNAME, hostname);
		method.addParameter(DOC_PAGE, page);
		method.addParameter(DOC_TITLE, title);
		try {
			// execute request
			int returnCode = client.executeMethod(method);
			return (returnCode == HttpStatus.SC_OK);

		} catch (Exception e) {
			logger.error("ERROR: {}", e.getMessage());
			return false;

		}
	}
	
	/**
	 * Executes post request to collect data on Exception Tracking
	 * in owner google analytics account.
	 * 
	 * @param description : String
	 * @param fatal : String
	 * @return boolean value, if request is ok then true else false.
	 */
	public boolean exceptionTracking(String description, boolean fatal) {
		//check input parameter
		if(description==null){
			logger.error("In Exception Tracking, Description is required.");
			return false;
		}
		// set request
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(ENDPOINT);
		method.addParameter(PROTOCAL_VERSION, "1");
		method.addParameter(TRACKING_ID, gaTrackingID);
		method.addParameter(CLIENT_ID, gaClientID);
		method.addParameter(HIT_TYPE, "exception");
		method.addParameter(APPLICATION_NAME, appName);
		method.addParameter(
				APPLICATION_VERSION,
				(!(this.appVersion == null || this.appVersion.length() == 0)) ? this.appVersion
						: DEFAULT_VERSION);
		method.addParameter(EXP_DESCRIPTION, description);
		method.addParameter(EXP_ISFATAL, ""+fatal);
		try {
			// execute request
			int returnCode = client.executeMethod(method);
			return (returnCode == HttpStatus.SC_OK);

		} catch (Exception e) {
			logger.error("ERROR: {}", e.getMessage());
			return false;

		}
	}
}
