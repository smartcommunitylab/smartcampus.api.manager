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

import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.trentorise.smartcampus.api.manager.model.ResultData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class RequestHandlerTest {

	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(RequestHandlerTest.class);
	private RestTemplate rtemplate;
	
	/**
	 * Set up method.
	 */
	@Before
	public void setUp() {
		rtemplate = new RestTemplate();
	}
	
	/**
	 * Test request handler
	 */
	@Test
	public void test(){
		String url = "http://proxy/vciao/hi";
		
		try {
			ResultData response = rtemplate.postForObject(
					"http://localhost:8080/apiManager/proxy",
					url, ResultData.class);
			log.info("Response: {}", response);
			
		} catch (HttpClientErrorException e) {
			log.info("Error: {}", e.getMessage());
			assertTrue("Error found", e.getMessage().contains("200"));
		}
		
	}
}
