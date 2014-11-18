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

/**
 * Google Analytics constant for post request on 
 * collection url.
 * The following parameter are required for Event Tracking request:
 * v=1             // Version.
 * &tid=UA-XXXX-Y  // Tracking ID / Property ID.
 * &cid=555        // Anonymous Client ID.
 * &t=event        // Event hit type
 * &ec=video       // Event Category. Required.
 * &ea=play        // Event Action. Required.
 * &el=holiday     // Event label.
 * &ev=300         // Event value.
 * 
 * Documentation: 
 * {@linkplain https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#event}
 * 
 * @author Giulia Canobbio
 *
 */
public class GoogleAnalyticsConstant {
	
	public static final String PROTOCAL_VERSION = "v";
	
	public static final String TRACKING_ID = "tid";
	
	public static final String CLIENT_ID = "cid";
	
	public static final String APPLICATION_NAME = "an";
	
	public static final String APPLICATION_VERSION = "av";
	
	public static final String DEFAULT_VERSION = "1";
	
	public static final String HIT_TYPE = "t";
	
	/*
	 * Event Tracking
	 */
	
	public static final String EVENT_CATEGORY = "ec";
	
	public static final String EVENT_ACTION= "ea";
	
	public static final String EVENT_LABEL= "el";
	
	public static final String EVENT_VALUE= "ev";
	
	/*
	 * Page Tracking
	 */
	
	public static final String DOC_HOSTNAME = "dh";
	
	public static final String DOC_PAGE = "dp";
	
	public static final String DOC_TITLE = "dt";
	
	/*
	 * Exception Tracking
	 */
	public static final String EXP_DESCRIPTION = "exd";
	
	public static final String EXP_ISFATAL = "exf";

}
