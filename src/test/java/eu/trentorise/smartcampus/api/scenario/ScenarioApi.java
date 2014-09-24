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
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SourceAddress;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.security.CustomAuthenticationException;

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
		r1.setUri("/resource1");
		r1.setVerb("GET");
		
		log.info("Add Resource 2..");
		Resource r2 = new Resource();
		r2.setName("Like");
		r2.setUri("/resource2");
		r2.setVerb("GET");
		
		//api
		log.info("Add api...");
		Api api = new Api();
		api.setId("api1");
		api.setName("Geocoding");
		api.setBasePath("/v0/geocoding");
		api.setOwnerId("1");

		Api nApi = apiManager.addApi(api);
		assertNotNull("Error in saving a new api..",nApi);

		apiManager.addResourceApi("api1", r1);
		apiManager.addResourceApi("api1", r2);
		
		
		log.info("Add api test terminated.");
	}
	
	/**
	 * Add policy to resource.
	 */
	@Test
	public void addPolicyResource1(){
		log.info("Add policy to resource 1 ...");
		
		SpikeArrest p1 = new SpikeArrest();
		p1.setId("resource-p1");
		p1.setName("SpikeArrest-resource1");
		p1.setNotes("Some notes bla bla bla bla");
		p1.setType("Spike Arrest");
		p1.setRate("12ps");
		
		Resource r = apiManager.addPolicyResourceApi("api1", "resource1", p1);
		assertNotNull("Problem in saving",r);
		
	}
	
	/**
	 * Update policy to resource.
	 */
	@Test
	public void updatePolicyResource1(){
		log.info("Update policy to resource 1 ...");
		
		SpikeArrest p1 = new SpikeArrest();
		p1.setId("resource-p1");
		p1.setName("SpikeArrest-resource1");
		p1.setNotes("Some notes bla bla bla bla update");
		p1.setType("policy");
		p1.setRate("12ps");
		
		Resource r = apiManager.updatePolicyResourceApi("api1", "resource1", p1);
		assertNotNull("Problem in saving",r);
		
	}
	
	/**
	 * Delete policy to resource.
	 */
	@Test
	public void deletePolicyResource1(){
		log.info("Delete policy to resource 1 ...");
		Resource r = apiManager.deletePolicyResourceApi("api1", "resource1", "resource-p1");
		assertNotNull("Problem in saving",r);
		
	}
	
	/**
	 * Retrieves resource data from api. 
	 * Searching first by id and then by name.
	 */
	@Test
	public void retrieveResources(){
		log.info("Find resource api by resource id..");
		
		Resource r = apiManager.getResourceApiByResourceId("api1","resource1");
		assertNotNull("Error in finding by resource id", r);
		assertTrue("Incorrect id", r.getId().equalsIgnoreCase("resource1"));

		log.info("Find resource api by resource name..");
		List<Resource> rlist = apiManager.getResourceApiByResourceName("api1", "Like");
		assertNotNull("Error in finding by resource name", rlist);
		assertTrue("Incorrect name", rlist.get(0).getName().equalsIgnoreCase("Like"));
		
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
		r1.setUri("/resource1");
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
		Quota p1 = new Quota();
		p1.setId("api-p1");
		p1.setName("Quota-1");
		p1.setNotes("Some notes bla bla bla bla");
		p1.setType("Quota");
		p1.setAllowCount(12);
		p1.setInterval(2000);
		p1.setTimeUnit("second");
		
		log.info("Add policy 3.. Spike Arrest");
		SpikeArrest p3 = new SpikeArrest();
		//policy parameter
		p3.setName("SpikeArrest-3");
		p3.setNotes("Some notes bla bla bla bla");
		p3.setType("Spike Arrest");
		//spike arrest parameter
		p3.setRate("10pm");
		
		log.info("Add policy 4.. IP Access Control");
		IPAccessControl p4 = new IPAccessControl();
		p4.setId("p4-ip-access");
		p4.setName("IP p4");
		p4.setNotes("Notes about policy 4");
		p4.setType("IP Access Control");
		p4.setRule("ALLOW");
		
		List<SourceAddress> whiteList = new ArrayList<SourceAddress>();
		SourceAddress w1 = new SourceAddress();
		w1.setMask(32);
		w1.setIp("10.10.10.20");
		whiteList.add(w1);
		p4.setWhiteList(whiteList);
		
		List<SourceAddress> blackList = new ArrayList<SourceAddress>();
		SourceAddress b1 = new SourceAddress();
		b1.setMask(32);
		b1.setIp("10.10.10.10");
		blackList.add(b1);
		p4.setBlackList(blackList);
		
		log.info("Policy 1: {}", apiManager.addPolicyApi("api1", p1));
		
		Policy policy = apiManager.addPolicyApi("api1", p3);
		assertNotNull("Error in finding by api",policy);
		
		Policy ip = apiManager.addPolicyApi("api1", p4);
		assertNotNull("Error in finding by api",ip);
		
		log.info("Add policy api test terminated.");
	}
	
	/**
	 * Retrieves policy data from api. 
	 * Searching first by id, name, category and then type.
	 */
	@Test
	public void retrievePolicies() {
		log.info("Find policies api by policies id..");

		Quota p = (Quota) apiManager.getPolicyApiByPolicyId("api1", "api-p1");
		assertNotNull("Error in finding by resource id", p);
		assertTrue("Incorrect id", p.getId().equalsIgnoreCase("api-p1"));

		log.info("Find policies api by policies name..");
		List<Policy> plist = apiManager.getPolicyApiByPolicyName("api1",
				"Quota-1");
		assertNotNull("Error in finding by resource name", plist);
		assertTrue("Incorrect name",
				plist.get(0).getName().equalsIgnoreCase("Quota-1"));

		log.info("Find policies api by policies category..");
		List<Policy> pclist = apiManager.getPolicyApiByPolicyCategory("api1",
				"quality");
		assertNotNull("Error in finding by resource name", pclist);
		assertTrue("Incorrect category", pclist.get(0).getCategory()
				.equalsIgnoreCase("quality"));

		log.info("Find policies api by policies type..");
		List<Policy> ptlist = apiManager.getPolicyApiByPolicyType("api1",
				"Spike Arrest");
		assertNotNull("Error in finding by resource name", ptlist);
		assertTrue("Incorrect type",
				ptlist.get(0).getType().equalsIgnoreCase("Spike Arrest"));

		log.info("Search terminated.");
	}
	
	/**
	 * Update one of api policies
	 */
	@Test
	public void updatePolicy1(){
		log.info("Update policy 1 of api...");
		
		Quota p1 = new Quota();
		p1.setId("api-p1");
		p1.setName("Quota-1");
		p1.setNotes("Some notes bla bla bla bla update");
		p1.setType("Quota");
		p1.setAllowCount(12);
		p1.setInterval(4000);
		p1.setTimeUnit("second");
		
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
		//app1.setKey("junit.test.openservice.1.app");
		
		log.info("Add app 2..");
		App app2 = new App();
		app2.setName("Smartcampus app junit-test");
		//app2.setKey("junit.test.smartcampus.2.app");
		
		apiManager.addApp(app1);
		App app = apiManager.addApp(app2);
		assertNotNull("Error in finding app",app);
		
		log.info("Add app api test terminated.");
	}
	
	/**
	 * Retrieves apps by id, key and name
	 */
	@Test
	public void retrieveApps() {
		log.info("Find apps api by apps id..");

		App app = apiManager.getAppById("junit-test-spring-1");

		assertNotNull("Error in finding by resource id", app);
		assertTrue("Incorrect id",
				app.getId().equalsIgnoreCase("junit-test-spring-1"));

		log.info("Find apps api by apps name..");
		List<App> app1 = apiManager.getAppByName("Smartcampus app junit-test");
		assertNotNull("Error in finding by resource id", app1);
		assertTrue(
				"Incorrect id",
				app1.get(0).getName()
						.equalsIgnoreCase("Smartcampus app junit-test"));

		log.info("Find apps api by apps key..");
		App a = apiManager.getAppById("junit-test-spring-1");
		List<App> app2 = apiManager.getAppByKey(a.getKey());
		assertNotNull("Error in finding by resource id", app2);
		assertTrue("Incorrect id",
				app2.get(0).getKey().equalsIgnoreCase(a.getKey()));

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
		
		apiManager.updateApp(app1);
		
		log.info("Update app 1 of api terminated.");
	}
	
	/**
	 * Deletes app 1 from api
	 */
	@Test
	public void deleteApp1(){
		log.info("Delete app 1 of api...");
		apiManager.deleteApp("junit-test-spring-1");
		
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
