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

import static org.junit.Assert.assertNotNull;

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

import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class AppManagerTest {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(AppManagerTest.class);
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
	 * Test add two new app data to collections.
	 */
	@Test
	public void addApp() {
		log.info("Add App data..");
		App app1 = new App();
		app1.setId("ue45fght");
		app1.setName("Openservice app");
		app1.setKey("openservice.98jhgndl.app");
		apiManager.addApp(app1);
		
		log.info("Add App 2..");
		App app2 = new App();
		app2.setName("Smartcampus app");
		app2.setKey("smartcampus.app.2");
		apiManager.addApp(app2);
		
		log.info("Add App data terminated.");
	}
	
	/**
	 * Test retrieving all App data.
	 */
	@Test
	public void listApp(){
		log.info("List App data..");
		List<App> lapp = apiManager.listApp();
		for(int i=0;i<lapp.size();i++){
			log.info("App id {}, ",lapp.get(i).getId());
		}
		assertNotNull("Error, null",lapp);
		log.info("List App data terminated.");
	}
	
	/**
	 * Test updating App data.
	 */
	@Test
	public void updateApi() {
		log.info("Update App ..");
		App app = new App();
		app.setId("ue45fght");
		app.setName("Openservice app");
		app.setKey("openservice.98jhgndl.app.upd");
		App nApp = apiManager.updateApp(app);
		assertNotNull("Error in saving a new app..",nApp);
		log.info("Update App terminated.");
	}
	
	/**
	 * Retrieves App searching by id.
	 */
	@Test
	public void getAppById(){
		log.info("Find App by id..");
		App app = apiManager.getAppById("ue45fght");
		assertNotNull("App not found", app);
		log.info("Found App by id terminated.");
	}
	
	/**
	 * Retrieves App searching by name.
	 */
	@Test
	public void getAppByName(){
		log.info("Find App by name..");
		List<App> lapp = apiManager.getAppByName("Openservice app");
		assertNotNull("App not found", lapp);
		for(int i=0;i<lapp.size();i++){
			log.info("App name {}, ",lapp.get(i).getName());
		}
		log.info("Found App by name terminated.");
	}
	
	/**
	 * Retrieves App searching by key.
	 */
	@Test
	public void getAppByKey(){
		log.info("Find App by key..");
		List<App> lapp = apiManager.getAppByKey("openservice.98jhgndl.app.upd");
		assertNotNull("Appa not found", lapp);
		for(int i=0;i<lapp.size();i++){
			log.info("App key {}, ",lapp.get(i).getKey());
		}
		log.info("Found App by key terminated.");
	}
	
	/**
	 * Deletes App data.
	 */
	@Test
	public void deleteApp(){
		log.info("Delete App..");
		App app = new App();
		app.setId("ue45fght");
		app.setName("Openservice app");
		app.setKey("openservice.98jhgndl.app.upd");
		apiManager.deleteApp(app);
		log.info("Delete App terminated.");
	}

}
