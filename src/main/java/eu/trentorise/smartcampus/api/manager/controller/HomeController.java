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
package eu.trentorise.smartcampus.api.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import eu.trentorise.smartcampus.api.manager.model.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.proxy.PolicyDecisionPoint;
import eu.trentorise.smartcampus.api.manager.proxy.RequestHandler;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	/**
	 * Instance of {@link RequestHandler}.
	 */
	@Autowired
	private RequestHandler requestHandler;
	/**
	 * Instance of {@link PolicyDecisionPoint}.
	 */
	@Autowired
	private PolicyDecisionPoint pdp;
	
	/**
	 * Return "HelloWorld" string
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		logger.info("Hello World!");
		logger.info("User {}",SecurityContextHolder.getContext().getAuthentication().getName());
		return "index";
	}
	
	/**
	 * Try Request Handler class, with retrieving api url from request.
	 * GET method.
	 * 
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link ResultData} with data, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"ok", otherwise "Error".
	 * 			If exception is threw then its error is returned.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResultData requestHandl(HttpServletRequest request){
		logger.info("-----------------------------------------------");
		logger.info("----------------START-------------------------------");
		
		try {
			RequestHandlerObject r = requestHandler.handleRequest(request);
			pdp.applyPoliciesBatch(r);

			logger.info("------------------END-----------------------------");
			logger.info("-----------------------------------------------");

			return new ResultData(r, HttpServletResponse.SC_OK, "ok");
			
			
		} catch (IllegalArgumentException i) {
			logger.info("Exception: {}", i.getMessage());
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND,
					i.getMessage());
		}catch(SecurityException s){
			logger.info("Security Exception: {}", s.getMessage());
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN,
					s.getMessage());
		}	

	}
	
}
