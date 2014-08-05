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

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import eu.trentorise.smartcampus.api.manager.model.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.proxy.PolicyDecisionPoint;
import eu.trentorise.smartcampus.api.manager.proxy.RequestHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-mongodb.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class ProxyTest {
	/**
	 * Instance of {@link Logger}.
	 */
	private Logger log = LoggerFactory.getLogger(ProxyTest.class);
	/**
	 * Instance of {@link RequestHandler}.
	 */
	@Autowired
	private RequestHandler requestHandler;
	/**
	 * Instance of {@link PolicyDecisionPoint}.
	 */
	@Autowired
	private PolicyDecisionPoint pdecision;
	
	/**
	 * Set up method.
	 */
	@Before
	public void setUp() {

	}
	
	/**
	 * Test proxy scenario
	 */
	@Test
	public void test() {
		log.info("Test starting..");
		
		String url = "http://proxy/vciao/hi/r1/2/";
		//request handler
		log.info("Request hanlder..");
		RequestHandlerObject obj = requestHandler.handleUrl(url, null);
		
		//policy decision
		log.info("Policy Decision Port..");
		pdecision.applyPoliciesBatch(obj);
		
		log.info("Test end.");
	}

}
