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
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
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
	 * Update api and two policies.
	 */
	@Test
	public void addPolicy(){
		log.info("Add policy api test...");
		
		//Policy list
		log.info("Add policy 1...");
		Policy p1 = new Policy();
		p1.setId("api-p1");
		p1.setName("SpikeArrest-1");
		p1.setNotes("Some notes bla bla bla bla");
		p1.setCategory("quality");
		p1.setType("policy");
		
		log.info("Add policy 2..");
		Policy p2 = new Policy();
		p2.setName("Quota-1");
		p2.setNotes("Some notes of quota bla bla bla bla");
		p2.setCategory("quality");
		p2.setType("policy");
		
		log.info("Add policy 3.. Spike Arrest");
		SpikeArrest p3 = new SpikeArrest();
		//policy parameter
		p3.setName("SpikeArrest-3");
		p3.setNotes("Some notes bla bla bla bla");
		p3.setCategory("quality");
		p3.setType("policy");
		//spike arrest parameter
		p3.setRate("10pm");
		
		apiManager.addPolicyApi("api1", p1);
		apiManager.addPolicyApi("api1", p3);
		Api api = apiManager.addPolicyApi("api1", p2);
		assertNotNull("Error in finding by api",api);
		
		log.info("Add policy api test terminated.");
	}
	
	/**
	 * Retrieves policy data from api. 
	 * Searching first by id, name, category and then type.
	 */
	@Test
	public void retrievePolicies(){
		log.info("Find policies api by policies id..");
		Policy p= apiManager.getPolicyApiByPolicyId("api1", "api-p1");
		assertNotNull("Error in finding by resource id",p);
		assertTrue("Incorrect id",p.getId().equalsIgnoreCase("api-p1"));
		
		log.info("Find policies api by policies name..");
		List<Policy> plist = apiManager.getPolicyApiByPolicyName("api1", "Quota-1");
		assertNotNull("Error in finding by resource name",plist);
		assertTrue("Incorrect name",plist.get(0).getName().equalsIgnoreCase("Quota-1"));
		
		log.info("Find policies api by policies category..");
		List<Policy> pclist = apiManager.getPolicyApiByPolicyCategory("api1", "quality");
		assertNotNull("Error in finding by resource name",pclist);
		assertTrue("Incorrect category",pclist.get(0).getCategory().equalsIgnoreCase("quality"));
		
		log.info("Find policies api by policies type..");
		List<Policy> ptlist = apiManager.getPolicyApiByPolicyType("api1", "policy");
		assertNotNull("Error in finding by resource name",ptlist);
		assertTrue("Incorrect type",ptlist.get(0).getType().equalsIgnoreCase("policy"));
		
		log.info("Search terminated.");
	}
	
	/**
	 * Update one of api policies
	 */
	@Test
	public void updatePolicy1(){
		log.info("Update policy 1 of api...");
		
		Policy p1 = new Policy();
		p1.setId("api-p1");
		p1.setName("SpikeArrest-1");
		p1.setNotes("Some notes bla bla bla bla with update");
		p1.setCategory("quality");
		p1.setType("policy");
		apiManager.updatePolicyApi("api1", p1);
		
		log.info("Update policy 1 of api terminated.");
	}
	
	/**
	 * Delete one of api policies
	 */
	@Test
	public void deletePolicy1(){
		log.info("Delete policy 1 of api...");
		
		//get resource 1 and delete it
		apiManager.deletePolicyApi("api1", "api-p1");
		
		log.info("Delete policy 1 of api terminated.");
		
	}
	
	/**
	 * Add app and update api
	 */
	@Test
	public void addApp(){
		log.info("Add app api test...");
		
		//App list
		log.info("Add app 1...");
		App app1 = new App();
		app1.setId("junit-test-spring-1");
		app1.setName("Openservice app junit-test");
		app1.setKey("junit.test.openservice.1.app");
		
		log.info("Add app 2..");
		App app2 = new App();
		app2.setName("Smartcampus app junit-test");
		app2.setKey("junit.test.smartcampus.2.app");
		
		apiManager.addAppApi("api1", app1);
		Api api = apiManager.addAppApi("api1", app2);
		assertNotNull("Error in finding api",api);
		
		log.info("Add app api test terminated.");
	}
	
	/**
	 * Retrieves apps by id, key and name
	 */
	@Test
	public void retrieveApps(){
		log.info("Find apps api by apps id..");
		App app= apiManager.getAppApiByAppId("api1", "junit-test-spring-1");
		assertNotNull("Error in finding by resource id",app);
		assertTrue("Incorrect id",app.getId().equalsIgnoreCase("junit-test-spring-1"));
		
		log.info("Find apps api by apps name..");
		List<App> app1= apiManager.getAppApiByAppName("api1", "Smartcampus app junit-test");
		assertNotNull("Error in finding by resource id",app1);
		assertTrue("Incorrect id",app1.get(0).getName().equalsIgnoreCase("Smartcampus app junit-test"));
		
		log.info("Find apps api by apps key..");
		List<App> app2= apiManager.getAppApiByAppKey("api1", "junit.test.smartcampus.2.app");
		assertNotNull("Error in finding by resource id",app2);
		assertTrue("Incorrect id",app2.get(0).getKey().equalsIgnoreCase("junit.test.smartcampus.2.app"));
		
		log.info("Search terminated.");
	}
	
	/**
	 * Retrieves and update app 1
	 */
	@Test
	public void updateApp1(){
		log.info("Update app 1 of api...");
		
		App app1 = new App();
		app1.setId("junit-test-spring-1");
		app1.setName("Openservice app junit-test-update");
		app1.setKey("junit.test.openservice.1.app");
		apiManager.updateAppApi("api1", app1);
		
		log.info("Update app 1 of api terminated.");
	}
	
	/**
	 * Deletes app 1 from api
	 */
	@Test
	public void deleteApp1(){
		log.info("Delete app 1 of api...");
		
		apiManager.deleteAppApi("api1", "junit-test-spring-1");
		
		log.info("Delete app 1 of api terminated.");
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