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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
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
		r1.setUri("http://www.mydomain.it/resource1");
		r1.setVerb("GET");
		
		//Policy
		log.info("Add policy.. Spike Arrest");
		SpikeArrest p3 = new SpikeArrest();
		//policy parameter
		p3.setName("SpikeArrest-3");
		p3.setNotes("Some notes bla bla bla bla");
		p3.setCategory("quality");
		p3.setType("policy");
		//spike arrest parameter
		p3.setRate("10pm");
		
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
		
		List<Policy> plist = new ArrayList<Policy>();
		plist.add(p3);
		api.setPolicy(plist);
		
		List<App> alist = new ArrayList<App>();
		alist.add(app1);
		api.setApp(alist);
		
		//add rest
		try{
			ResultData response = rtemplate.postForObject("http://localhost:8080/apiManager/api/add", 
					api, ResultData.class);
			log.info("Response: {}",response);
			assertTrue("Successfully", response.getMessage().equalsIgnoreCase("Saved successfully."));
		}catch(HttpClientErrorException e){
			log.info("Error: {}",e.getMessage());
			assertTrue("Error found", !e.getMessage().contains("400"));
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
		// Resource
		log.info("Add resource...");
		Resource r1 = new Resource();
		r1.setId("resource1");
		r1.setName("Counter");
		r1.setUri("http://www.mydomain.it/resource1");
		r1.setVerb("GET");

		// Policy
		log.info("Add policy.. Spike Arrest");
		//SpikeArrest p3 = new SpikeArrest();
		Policy p3 = new Policy();
		// policy parameter
		p3.setName("SpikeArrest-3");
		p3.setNotes("Some notes bla bla bla bla");
		p3.setCategory("quality");
		p3.setType("policy");
		// spike arrest parameter
		//p3.setRate("10pm");

		// App
		log.info("Add app...");
		App app1 = new App();
		app1.setId("junit-test-spring-1");
		app1.setName("Openservice app junit-test");
		app1.setKey("junit.test.openservice.1.app");
			
		// api
		log.info("Update api...");
		Api api = new Api();
		api.setId("api1");
		api.setName("Geocoding");
		api.setBasePath("/v0/geocoding/update");
		api.setOwnerId("g1shjfdj");
		
		List<Resource> rlist = new ArrayList<Resource>();
		rlist.add(r1);
		api.setResource(rlist);
		
		List<Policy> plist = new ArrayList<Policy>();
		plist.add(p3);
		api.setPolicy(plist);
		
		List<App> alist = new ArrayList<App>();
		alist.add(app1);
		api.setApp(alist);
		
		try{
			ResultData response = rtemplate.postForObject("http://localhost:8080/apiManager/api/update", 
					api, ResultData.class);
			log.info("Response: {}",response.getData());
			log.info("Response: {}",response.getMessage());
			assertTrue("Successfully", response.getMessage().equalsIgnoreCase("Update successfully."));
		}catch(HttpClientErrorException e){
			log.info("Error: {}",e.getMessage());
			assertTrue("Error found", !e.getMessage().contains("400"));
			
		}
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
