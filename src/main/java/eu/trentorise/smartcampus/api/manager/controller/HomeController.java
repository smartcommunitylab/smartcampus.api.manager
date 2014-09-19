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
import eu.trentorise.smartcampus.api.security.CustomAuthenticationException;

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
	 * Try Request Handler class, with a string api url.
	 * POST method.
	 * 
	 * @param url : String 
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link ResultData} with data, 
	 * 			status (OK and BAD REQUEST) and a string message : 
	 * 			"ok", otherwise "Error".
	 */
	/*@RequestMapping(value="/proxy", method = RequestMethod.POST)
	@ResponseBody
	public ResultData requestHandler(@RequestBody String url, HttpServletRequest request){
		logger.info("proxy request handler");
		try {
			String decodedurl = URLDecoder.decode(url, "UTF-8");
			logger.info("Decoded url: {}", decodedurl);
			RequestHandlerObject r = requestHandler.handleUrl(decodedurl, request);
			pdp.applyPoliciesBatch(r);
			
			if(r.getApiId()==null && r.getResourceId()==null){
				return new ResultData(r, HttpServletResponse.SC_NOT_FOUND, "not found");
			}else 
				return new ResultData(r, HttpServletResponse.SC_OK, "ok");
			
		} catch (IOException e) {
			e.printStackTrace();
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, "Error");
		}
	}*/
	
	/**
	 * Try Request Handler class, with retrieving api url from request.
	 * GET method.
	 * 
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link ResultData} with data, 
	 * 			status (OK, BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"ok", otherwise "Error".
	 * 			If security error message is threw then exception error is returned.
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}

		

	}
	
	/**
	 * 
	 * @param appId
	 * @param request
	 * @return
	 */
	/*@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResultData requestHandl(@RequestBody String appId, HttpServletRequest request){
		logger.info("proxy request handler");

		RequestHandlerObject r = requestHandler.handleRequestWithAppId(appId, request);
		pdp.applyPoliciesBatch(r);
		
		if(r.getApiId()==null && r.getResourceId()==null){
			return new ResultData(r, HttpServletResponse.SC_NOT_FOUND, "not found");
		}else 
			return new ResultData(r, HttpServletResponse.SC_OK, "ok");

	}*/
	
}
