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
package eu.trentorise.smartcampus.api.manager;

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
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Testing api manager.
 * 
 * @author Giulia Canobbio
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class ApiManagerTest {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(ApiManagerTest.class);
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
	 * Test add two new api data to collections.
	 * See behavior of mongo on data without key: 
	 * Mongo does not save object without id, therefore 
	 * an Mongo object id is created during creation.
	 */
	@Test
	public void addApi() {
		log.info("Add a new Api ..");
		Api api = new Api();
		api.setId("ndkdhfkdifuret94860936093");
		api.setName("Geocoding");
		api.setBasePath("/v0/geocoding");
		api.setOwnerId("junit-test");
		Api nApi = apiManager.addApi(api);
		assertNotNull("Error in saving a new api..",nApi);
		
		log.info("Add second new Api ..");
		Api api2 = new Api();
		api2.setName("Transportation System Information");
		api2.setBasePath("/v1/mobility");
		api2.setOwnerId("junit-test");
		Api n2Api = apiManager.addApi(api2);
		assertNotNull("Error in saving a new api..",n2Api);
		
		Api api3 = new Api();
		api3.setName("Without key");
		api3.setBasePath("/v3/noKey");
		api3.setOwnerId("junit-test");
		Api n3Api = apiManager.addApi(api3);
		assertNotNull("Error in saving a new api..",n3Api);
		log.info("Add a new Api terminated.");
	}
	
	/**
	 * Test retrieving all api data.
	 */
	@Test
	public void listApi(){
		log.info("List api data..");
		List<Api> lapi = apiManager.listApi();
		for(int i=0;i<lapi.size();i++){
			log.info("Api id {}, ",lapi.get(i).getId());
		}
		assertNotNull("Error, null",lapi);
		log.info("List api data terminated.");
	}
	
	/**
	 * Test updating api data.
	 */
	@Test
	public void updateApi() {
		log.info("Add a new Api ..");
		Api api = new Api();
		api.setId("ndkdhfkdifuret94860936093");
		api.setName("Geocoding update");
		api.setBasePath("/v0/geocoding");
		api.setOwnerId("g1shjfdj");
		Api nApi = apiManager.addApi(api);
		assertNotNull("Error in saving a new api..",nApi);
	}
	
	/**
	 * Retrieves api searching by id.
	 */
	@Test
	public void getApiById(){
		log.info("Find api by id..");
		Api api = apiManager.getApiById("ndkdhfkdifuret94860936093");
		assertNotNull("Api not found", api);
		log.info("Found api by id terminated.");
		
	}
	
	/**
	 * Retrieves api searching by base path
	 */
	@Test
	public void getApiByBasePath(){
		log.info("Find api by id..");
		List<Api> api = apiManager.getApiByBasePath("/v0/geocoding");
		assertNotNull("Api not found", api);
		assertTrue("Api not found", api.size()>0);
		for(int i=0;i<api.size();i++){
			log.info("Api {}",i);
			log.info("Name {}",api.get(i).getName());
			log.info("Basepath {}",api.get(i).getBasePath());
		}
		log.info("Found api by id terminated.");
	}
	
	/**
	 * Retrieves api searching by owner id
	 */
	@Test
	public void getApiByOwnerId(){
		log.info("Find api by id..");
		List<Api> api = apiManager.getApiByOwnerId("junit-test");
		assertNotNull("Api not found", api);
		log.info("Found api by id terminated.");
	}
	
	/**
	 * Deletes api data.
	 */
	@Test
	public void deleteApi(){
		log.info("Delete api..");
		Api api = new Api();
		api.setId("ndkdhfkdifuret94860936093");
		api.setName("Geocoding");
		api.setBasePath("/v0/geocoding");
		
		apiManager.deleteApi(api);
			
		List<Api> apilist = apiManager.getApiByOwnerId("junit-test");
		for (int i = 0; i < apilist.size(); i++) {
			apiManager.deleteApi(apilist.get(i));
		}

		log.info("Delete api terminated.");
		
	}

}
