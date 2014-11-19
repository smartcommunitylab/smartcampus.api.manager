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
package eu.trentorise.smartcampus.api.analytics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import eu.trentorise.smartcampus.api.manager.googleAnalytics.GoogleAnalyticsPostCollect;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
/**
 * Test Measurement Protocol of Google Analytics to
 * post collect of page, event and exceptiont tracking.
 * 
 * @author Giulia Canobbio
 *
 */
public class GoogleMeasurementProtocolTest {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(GoogleMeasurementProtocolTest.class);
	
	GoogleAnalyticsPostCollect gatemplate;
	
	@Before
	public void setUp() {
		gatemplate = new GoogleAnalyticsPostCollect("<insert here Tracking ID>", "Test Api Manager", "1");
	}
	
	@Test
	public void testEventTracking(){
		log.info("Event tracking test..");
		boolean request;
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #1: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #2: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #3: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Denied", "Geocoding", "1");
		log.info("Request #4: {}",request);
		request =gatemplate.eventTracking("API", "Access Denied", "Geocoding", "1");
		log.info("Request #5: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #6: {}",request);
		request =gatemplate.eventTracking("API", "Access Denied", "Geocoding", "1");
		log.info("Request #7: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #8: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #9: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #10: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Denied", "Geocoding", "1");
		log.info("Request #11: {}",request);
		request =gatemplate.eventTracking("API", "Access Denied", "Geocoding", "1");
		log.info("Request #12: {}",request);
	}
	
	@Test
	public void testEventTrackingResource(){
		log.info("Event tracking test..");
		boolean request;
		
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding/resource1", "1");
		log.info("Request #1: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding", "1");
		log.info("Request #2: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Geocoding/resource2", "1");
		log.info("Request #3: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Denied", "Geocoding/resource3", "1");
		log.info("Request #4: {}",request);
		request =gatemplate.eventTracking("API", "Access Denied", "Geocoding/resource1", "1");
		log.info("Request #5: {}",request);
	}
	
	@Test
	public void testEventTracking2(){
		log.info("Event tracking test..");
		boolean request;
		request =gatemplate.eventTracking("API", "Access Granted", "Sample", "1");
		log.info("Request #1: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Sample", "1");
		log.info("Request #2: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Sample", "1");
		log.info("Request #3: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Denied", "Sample", "1");
		log.info("Request #4: {}",request);
		request =gatemplate.eventTracking("API", "Access Denied", "Sample", "1");
		log.info("Request #5: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Granted", "Sample/r1", "1");
		log.info("Request #6: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Sample", "1");
		log.info("Request #7: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Granted", "Sample/r2", "1");
		log.info("Request #8: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Sample", "1");
		log.info("Request #9: {}",request);
		request =gatemplate.eventTracking("API", "Access Granted", "Sample", "1");
		log.info("Request #10: {}",request);
		
		request =gatemplate.eventTracking("API", "Access Denied", "Sample/r2", "1");
		log.info("Request #11: {}",request);
		request =gatemplate.eventTracking("API", "Access Denied", "Sample", "1");
		log.info("Request #12: {}",request);
	}
	
	@Test
	public void testExceptionTracking(){
		log.info("Exception tracking test..");
		boolean request;
		request =gatemplate.exceptionTracking("Sample IOException", false);
		log.info("Request #1: {}",request);
		
		request =gatemplate.exceptionTracking("Sample SecurityException", true);
		log.info("Request #2: {}",request);
		
		request =gatemplate.exceptionTracking("Sample IOException", false);
		log.info("Request #3: {}",request);
		
		request =gatemplate.exceptionTracking("Sample IOException", true);
		log.info("Request #4: {}",request);
	}
	
	@Test
	public void testExceptionTracking2(){
		log.info("Exception tracking test..");
		boolean request;
		request =gatemplate.exceptionTracking("Geocoding IOException", false);
		log.info("Request #1: {}",request);
		
		request =gatemplate.exceptionTracking("Geocoding SecurityException", true);
		log.info("Request #2: {}",request);
		
		request =gatemplate.exceptionTracking("Geocoding IOException", false);
		log.info("Request #3: {}",request);
		
		request =gatemplate.exceptionTracking("Geocoding IOException", true);
		log.info("Request #4: {}",request);
	}

}
