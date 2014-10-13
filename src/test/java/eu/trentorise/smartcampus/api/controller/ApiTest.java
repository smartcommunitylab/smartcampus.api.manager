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
package eu.trentorise.smartcampus.api.controller;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.policy.Quota;
import eu.trentorise.smartcampus.api.manager.model.policy.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.util.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Testing controller rest service
 * for adding, update and delete api data.
 * 
 * @author Giulia Canobbio
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class ApiTest {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(ApiTest.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager apiManager;
	/**
	 * Instance of {@link RestTemplate}.
	 */
	private RestTemplate rtemplate;

	/**
	 * Set up method.
	 */
	@Before
	public void setUp() {
		rtemplate = new RestTemplate();
	}
	
	/**
	 * Add api data by rest service.
	 * TODO It does not work - 400 BAD REQUEST
	 */
	@Test
	public void addApi(){
		log.info("Add api data by rest service");
		
		// Resource
		log.info("Add resource...");
		Resource r1 = new Resource();
		r1.setId("resource1");
		r1.setName("Counter");
		r1.setUri("/resource1");
		r1.setVerb("GET");
		
		//App
		log.info("Add app...");
		App app1 = new App();
		app1.setId("junit-test-spring-1");
		app1.setName("Openservice app junit-test");
		app1.setKey("junit.test.openservice.1.app");

		// api
		log.info("Add api...");
		Api api = new Api();
		api.setId("api1");
		api.setName("Geocoding");
		api.setBasePath("/v0/geocoding");
		api.setOwnerId("g1shjfdj");
		
		List<Resource> rlist = new ArrayList<Resource>();
		rlist.add(r1);
		api.setResource(rlist);
		
		
		//add rest
		try{
			ResultData response = rtemplate.postForObject("http://localhost:8080/apiManager/api/add", 
					api, ResultData.class);
			log.info("Response: {}",response);
			assertTrue("Successfully", response.getMessage().equalsIgnoreCase("Saved successfully."));
		}catch(HttpClientErrorException e){
			log.info("Error: {}",e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}
	}
	
	/**
	 * Retrieves api data by owner id.
	 */
	@Test
	public void getApiByOwnerId(){
		log.info("Retrieves api by owner id..");
		ResultData response = rtemplate.getForObject("http://localhost:8080/apiManager/api/g1shjfdj",
				ResultData.class, new Object[]{});
		
		log.info("Api: {}",response.getData());
				
		assertNotNull("No api for this owner", response.getData());
		
	}
	
	/**
	 * Updates api.
	 */
	@Test
	public void updateApi(){
		// api
		log.info("Update api...");
		Api api = new Api();
		api.setId("api1");
		api.setName("Geocoding");
		api.setBasePath("/v0/geocoding/update");
		api.setOwnerId("g1shjfdj");
		
		try{
			ResultData response = rtemplate.postForObject("http://localhost:8080/apiManager/api/update", 
					api, ResultData.class);
			log.info("Response: {}",response.getData());
			log.info("Response: {}",response.getMessage());
			assertTrue("Successfully", response.getMessage().equalsIgnoreCase("Update successfully."));
		}catch(HttpClientErrorException e){
			log.info("Error: {}",e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
			
		}
	}
	
	/**
	 * Add and update a resource to api
	 */
	@Test
	public void addUpdateResource(){
		log.info("Add and update resource ...");
		Resource r2 = new Resource();
		r2.setId("resource2");
		r2.setName("Resource-junit");
		r2.setUri("/resource2");
		r2.setVerb("GET");
		
		//add
		try{
			ResultData response = rtemplate.postForObject(
					"http://localhost:8080/apiManager/api/add/api1/resource", 
					r2, 
					ResultData.class);
			log.info("Response: {}",response.getData());
			log.info("Response: {}",response.getMessage());
			assertTrue("Successfully", response.getMessage().contains("successfully"));
		}catch(HttpClientErrorException e){
			log.info("Error: {}",e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}
		
		//update
		r2.setUri("http://www.mydomain.it/resource2/update");
		try{
			ResultData response = rtemplate.postForObject(
					"http://localhost:8080/apiManager/api/update/api1/resource", 
					r2, 
					ResultData.class);
			log.info("Response: {}",response.getData());
			log.info("Response: {}",response.getMessage());
			assertTrue("Successfully", response.getMessage().contains("successfully"));
		}catch(HttpClientErrorException e){
			log.info("Error: {}",e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}
		
		//delete
		rtemplate.delete("http://localhost:8080/apiManager/api/delete/api1/resource/"+r2.getId());
	}
	
	/**
	 * Add and update an app to api
	 */
	@Test
	public void addUpdateApp(){
		log.info("Add and update app api...");
		App app2 = new App();
		app2.setId("junit-test-spring-2");
		app2.setName("Second app junit-test");
		app2.setKey("junit.test.openservice.2.app");
		
		// add
		try {
			ResultData response = rtemplate.postForObject(
					"http://localhost:8080/apiManager/api/add/api1/app",
					app2, ResultData.class);
			log.info("Response: {}", response.getData());
			log.info("Response: {}", response.getMessage());
			assertTrue(
					"Successfully",
					response.getMessage().contains(
							"successfully"));
		} catch (HttpClientErrorException e) {
			log.info("Error: {}", e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}

		// update
		app2.setName("Second app junit-test-update");
		try {
			ResultData response = rtemplate
					.postForObject(
							"http://localhost:8080/apiManager/api/update/api1/app",
							app2, ResultData.class);
			log.info("Response: {}", response.getData());
			log.info("Response: {}", response.getMessage());
			assertTrue(
					"Successfully",
					response.getMessage().contains(
							"successfully"));
		} catch (HttpClientErrorException e) {
			log.info("Error: {}", e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}
		
		//delete
		rtemplate.delete("http://localhost:8080/apiManager/api/delete/api1/app/"+app2.getId());
	}
	
	/**
	 * Add and update a policy to api
	 */
	@Test
	public void addUpdatePolicy(){
		log.info("Add and update policy api...");
		
		//Spike Arrest
		SpikeArrest p2 = new SpikeArrest();
		//policy parameter
		p2.setName("SpikeArrest-1");
		p2.setNotes("Spike arrest 1 notes");
		p2.setType("policy");
		//spike arrest parameter
		p2.setRate("10pm");
		
		// add
		try {
			ResultData response = rtemplate.postForObject(
					"http://localhost:8080/apiManager/api/add/api1/policy/spikeArrest", p2,
					ResultData.class);
			log.info("Response: {}", response.getData());
			log.info("Response: {}", response.getMessage());
			assertTrue(
					"Successfully",
					response.getMessage().contains(
							"successfully"));
		} catch (HttpClientErrorException e) {
			log.info("Error: {}", e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}
		
		//Quota
		Quota p3 = new Quota();
		// policy parameter
		p3.setId("p3-test");
		p3.setName("Quota-1");
		p3.setNotes("Quota 1 notes");
		p3.setType("policy");
		// spike arrest parameter
		p3.setInterval(1);
		p3.setTimeUnit("hour");
		p3.setAllowCount(99);
		
		// add
		try {
			ResultData response = rtemplate.postForObject(
					"http://localhost:8080/apiManager/api/add/api1/policy/quota", p3,
					ResultData.class);
			log.info("Response: {}", response.getData());
			log.info("Response: {}", response.getMessage());
			assertTrue(
					"Successfully",
					response.getMessage().contains(
							"successfully"));
		} catch (HttpClientErrorException e) {
			log.info("Error: {}", e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}
		
		//delete
		rtemplate.delete("http://localhost:8080/apiManager/api/delete/api1/policy/"+p3.getId());
	}
	
	/**
	 * Deletes api data from database.
	 */
	@Test
	public void deleteApi(){
		log.info("Delete api..");
		rtemplate.delete("http://localhost:8080/apiManager/api/delete/api1");
	}
	

}
