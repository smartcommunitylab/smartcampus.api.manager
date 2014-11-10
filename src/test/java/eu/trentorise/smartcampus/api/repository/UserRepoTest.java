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
 ******************************************************************************/package eu.trentorise.smartcampus.api.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import eu.trentorise.smartcampus.api.manager.model.User;
import eu.trentorise.smartcampus.api.manager.repository.UserRepository;

 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
 		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
 		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
 @TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class UserRepoTest {
	 
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(UserRepoTest.class);
	/**
	 * Instance of {@link UserRepository}
	 */
	@Autowired
	private UserRepository urepo;
	

	/**
	 * Set up method.
	 */
	@Before
	public void setUp() {

	}
	/**
	 * Save one user with role provider.
	 */
	@Test
	public void saveUser1() {
		//name="1" password="1" authorities="ROLE_PROVIDER"
		User u = new User();
		u.setId("1");
		u.setUsername("1");
		u.setPassword("1");
		u.setEnabled(1);
		u.setRole("ROLE_PROVIDER");
		
		User savedU = urepo.save(u);
		
		log.info("{}", savedU);
	}
	
	/**
	 * Save a second user with role provider
	 */
	@Test
	public void saveUser2() {
		//name="2" password="2" authorities="ROLE_PROVIDER"
		User u = new User();
		u.setId("2");
		u.setUsername("2");
		u.setPassword("2");
		u.setEnabled(1);
		u.setRole("ROLE_PROVIDER");
		
		u.setEmail("null1");
		
		User savedU = urepo.save(u);
		
		log.info("{}", savedU);
	}
	
	/**
	 * Save a third user with different role.
	 */
	@Test
	public void saveUser3() {
		//name="3" password="3" authorities="ROLE_USER"
		User u = new User();
		u.setId("3");
		u.setUsername("3");
		u.setPassword("3");
		u.setEnabled(1);
		u.setRole("ROLE_USER");
		
		u.setEmail("null2");
		
		User savedU = urepo.save(u);
		
		log.info("{}", savedU);
	}
	
	/**
	 * Test find by id and find by username
	 */
	@Test
	public void findUsers(){
		User u1 = urepo.findById("1");
		
		log.info("Find by id: {}", u1.getId());
		
		User u2 = urepo.findByUsername("2");
		
		log.info("Find by username: {}", u2.getUsername());
	}
	
	/**
	 * First create a fourth user and then
	 * delete it.
	 */
	@Test
	public void deleteUser(){
		User u = new User();
		u.setUsername("4");
		u.setPassword("4");
		u.setEnabled(0);
		u.setRole("ROLE_USER");
		
		u.setEmail("null3");
		
		User savedU = urepo.save(u);
		
		log.info("{}", savedU.getUsername());
		
		urepo.delete(savedU);
		
		User findu = urepo.findByUsername("4");
		
		log.info("Not found: {}", findu);
		
	}

}
