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
package eu.trentorise.smartcampus.api.scenario;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

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

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Scenario 1:
 * User adds an api called Geocoding with two resources: Counter and Like.
 * Then he/she decides to modify resource Counter from GET to POST.
 * After that he/she finds out that his/her api and resources are not needed anymore and
 * deletes them.
 * 
 * @author Giulia Canobbio
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class ScenarioApi {

	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(ScenarioApi.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager apiManager;
	
	/**
	 * Set up method.
	 */
	@Before
	public void setUp() {

	}
	
	/**
	 * Add api and two resources.
	 */
	@Test
	public void addApi(){
		log.info("Add api test...");
		
		//Resource list
		log.info("Add resource 1...");
		Resource r1 = new Resource();
		r1.setId("resource1");
		r1.setName("Counter");
		r1.setUri("http://www.mydomain.it/resource1");
		r1.setVerb("GET");
		
		log.info("Add Resource 2..");
		Resource r2 = new Resource();
		r2.setName("Like");
		r2.setUri("http://www.mydomain.it/resource2");
		r2.setVerb("GET");
		
		//api
		log.info("Add api...");
		Api api = new Api();
		api.setId("api1");
		api.setName("Geocoding");
		api.setBasePath("/v0/geocoding");
		api.setOwnerId("g1shjfdj");

		Api nApi = apiManager.addApi(api);
		assertNotNull("Error in saving a new api..",nApi);
		
		apiManager.addResourceApi("api1", r1);
		apiManager.addResourceApi("api1", r2);
		
		log.info("Add api test terminated.");
	}
	
	/**
	 * Retrieves resource data from api. 
	 * Searching first by id and then by name.
	 */
	@Test
	public void retrieveResources(){
		log.info("Find resource api by resource id..");
		Resource r = apiManager.getResourceApiByResourceId("api1", "resource1");
		assertNotNull("Error in finding by resource id",r);
		assertTrue("Incorrect id",r.getId().equalsIgnoreCase("resource1"));
		
		log.info("Find resource api by resource name..");
		List<Resource> rlist = apiManager.getResourceApiByResourceName("api1", "Like");
		assertNotNull("Error in finding by resource name",rlist);
		assertTrue("Incorrect name",rlist.get(0).getName().equalsIgnoreCase("Like"));
		
		log.info("Search terminated.");
	}
	
	/**
	 * Update one of api resources
	 */
	@Test
	public void updateResource1(){
		log.info("Update resource 1 of api...");
		
		Resource r1 = new Resource();
		r1.setId("resource1");
		r1.setName("Counter");
		r1.setUri("http://www.mydomain.it/resource1");
		r1.setVerb("POST");
		apiManager.updateResourceApi("api1", r1);
		
		log.info("Update resource 1 of api terminated.");
	}
	
	/**
	 * Delete one of api resources
	 */
	@Test
	public void deleteResource1(){
		log.info("Delete resource 1 of api...");
		
		//get resource 1 and delete it
		apiManager.deleteResourceApi("api1", "resource1");
		
		log.info("Delete resource 1 of api terminated.");
		
	}
	
	/**
	 * Delete api and resources
	 */
	@Test
	public void deleteAll(){
		log.info("Delete resources and api...");
		//retrieve api by id
		log.info("Retrieve api");
		Api api = apiManager.getApiById("api1");

		apiManager.deleteApi(api);
		
		log.info("Delete resources and api terminated.");
	}
	
	
}
