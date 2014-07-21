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

import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Testing policy manager.
 * 
 * @author Giulia Canobbio
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class PolicyManagerTest {
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(PolicyManagerTest.class);
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
	 * Test creating policy data.
	 */
	@Test
	public void addPolicy() {
		log.info("Add Policy data..");
		Policy p1 = new Policy();
		p1.setId("sa1");
		p1.setName("SpikeArrest");
		p1.setCategory("quality");
		p1.setType("t1");
		apiManager.addPolicy(p1);
		
		log.info("Add Policy 2..");
		Policy p2 = new Policy();
		p2.setId("sa2");
		p2.setName("Quota");
		p2.setCategory("quality");
		p2.setType("q1");
		apiManager.addPolicy(p2);
		
		log.info("Add Policy data terminated.");
	}
	
	/**
	 * Test retrieving all Policy data.
	 */
	@Test
	public void listPolicy(){
		log.info("List Policy data..");
		List<Policy> plist = apiManager.listPolicy();
		for(int i=0;i<plist.size();i++){
			log.info("Policy id {}, ",plist.get(i).getId());
		}
		assertNotNull("Error, null",plist);
		log.info("List Policy data terminated.");
	}
	
	/**
	 * Test updating Policy data.
	 */
	@Test
	public void updatePolicy() {
		log.info("Update Policy ..");
		Policy p1 = new Policy();
		p1.setId("sa1");
		p1.setName("SpikeArrest_update");
		p1.setCategory("quality");
		p1.setType("t1");
		Policy np = apiManager.updatePolicy(p1);
		assertNotNull("Error in saving a new Policy..",np);
		log.info("Update Policy terminated.");
	}
	
	/**
	 * Retrieves Policy searching by id.
	 */
	@Test
	public void getPolicyById(){
		log.info("Find Policy by id..");
		Policy p = apiManager.getPolicyById("sa1");
		assertNotNull("Policy not found", p);
		log.info("Found Policy by id terminated.");
	}
	
	/**
	 * Retrieves Policy searching by name.
	 */
	@Test
	public void getPolicyByName(){
		log.info("Find Policy by name..");
		List<Policy> plist = apiManager.getPolicyByName("SpikeArrest_update");
		assertNotNull("Policy not found", plist);
		for(int i=0;i<plist.size();i++){
			log.info("Policy name {}, ",plist.get(i).getName());
		}
		log.info("Found Policy by name terminated.");
	}
	
	/**
	 * Retrieves Policy searching by category.
	 */
	@Test
	public void getPolicyByCategory(){
		log.info("Find Policy by category..");
		List<Policy> plist = apiManager.getPolicyByCategory("quality");
		assertNotNull("Policy not found", plist);
		for(int i=0;i<plist.size();i++){
			log.info("Policy category {}, ",plist.get(i).getCategory());
		}
		log.info("Found Policy by category terminated.");
	}
	
	/**
	 * Retrieves Policy searching by type.
	 */
	@Test
	public void getPolicyByType(){
		log.info("Find Policy by type..");
		List<Policy> plist = apiManager.getPolicyByType("t1");
		assertNotNull("Policy not found", plist);
		for(int i=0;i<plist.size();i++){
			log.info("Policy type {}, ",plist.get(i).getType());
		}
		log.info("Found Policy by type terminated.");
	}
	
	/**
	 * Deletes Policy data.
	 */
	@Test
	public void deletePolicy(){
		log.info("Delete Policy..");
		Policy p1 = new Policy();
		p1.setId("sa1");
		p1.setName("SpikeArrest_update");
		p1.setCategory("quality");
		p1.setType("t1");
		apiManager.deletePolicy(p1);
		log.info("Delete Policy terminated.");
	}
}
