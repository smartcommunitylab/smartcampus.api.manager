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

import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class ResourceManagerTest {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(ResourceManagerTest.class);
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
	 * Test creating resource data.
	 */
	@Test
	public void addResource() {
		log.info("Add Resource data..");
		Resource r1 = new Resource();
		r1.setId("resource1");
		r1.setName("Counter-junit-test");
		r1.setUri("http://www.mydomain.it/resource1");
		r1.setVerb("GET");
		apiManager.addResource(r1);
		
		log.info("Add Resource 2..");
		Resource r2 = new Resource();
		r2.setName("Like-junit-test");
		r2.setUri("http://www.mydomain.it/resource2");
		r2.setVerb("GET");
		apiManager.addResource(r2);
		
		log.info("Add Resource data terminated.");
	}
	
	/**
	 * Test retrieving all Resource data.
	 */
	@Test
	public void listResource(){
		log.info("List Resource data..");
		List<Resource> rlist = apiManager.listResource();
		for(int i=0;i<rlist.size();i++){
			log.info("Resource id {}, ",rlist.get(i).getId());
		}
		assertNotNull("Error, null",rlist);
		log.info("List Resource data terminated.");
	}
	
	/**
	 * Test updating Resource data.
	 */
	@Test
	public void updateResource() {
		log.info("Update Resource ..");
		Resource r1 = new Resource();
		r1.setId("resource1");
		r1.setName("Counter-junit-test");
		r1.setUri("http://www.mydomain.it/resource1update");
		r1.setVerb("GET");
		Resource np = apiManager.updateResource(r1);
		assertNotNull("Error in saving a new Resource..",np);
		log.info("Update Resource terminated.");
	}
	
	/**
	 * Retrieves Resource searching by id.
	 */
	@Test
	public void getResourceById(){
		log.info("Find Resource by id..");
		Resource p = apiManager.getResourceById("resource1");
		assertNotNull("Resource not found", p);
		log.info("Found Resource by id terminated.");
	}
	
	/**
	 * Retrieves Resource searching by name.
	 */
	@Test
	public void getResourceByName(){
		log.info("Find Resource by name..");
		List<Resource> rlist = apiManager.getResourceByName("Like-junit-test");
		assertNotNull("Resource not found", rlist);
		for(int i=0;i<rlist.size();i++){
			log.info("Resource name {}, ",rlist.get(i).getName());
		}
		log.info("Found Resource by name terminated.");
	}
	
	/**
	 * Deletes Resource data.
	 */
	@Test
	public void deleteResource(){
		log.info("Delete Resource..");
		Resource r1 = new Resource();
		r1.setId("resource1");
		r1.setName("Counter-junit-test");
		r1.setUri("http://www.mydomain.it/resource1update");
		r1.setVerb("GET");
		apiManager.deleteResource(r1);
		
		Resource r2 = apiManager.getResourceByName("Like-junit-test").get(0);
		apiManager.deleteResource(r2);
		
		log.info("Delete Resource terminated.");
	}

}
