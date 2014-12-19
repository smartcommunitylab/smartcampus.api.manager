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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import eu.trentorise.smartcampus.api.manager.model.util.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.model.util.ResultData;
import eu.trentorise.smartcampus.api.manager.proxy.PolicyDecisionPoint;
import eu.trentorise.smartcampus.api.manager.proxy.RequestHandler;

/**
 * Handles requests for the application home page.
 */
@Controller
@PropertySource("classpath:apimanager.properties")
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
	 * Instance of {@link Environment}
	 */
	@Inject
	private Environment env;
	
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
	 * Authenticates user and returns home
	 * 
	 * @param name : String, a path variable
	 * @param request : instance of {@link HttpServletRequest}
	 * @return index jsp
	 */
	@RequestMapping(value = "/home/{name}", method = RequestMethod.GET)
	public String homeCallback(@PathVariable String name, HttpServletRequest request) {
		logger.info("Hello Home with name: {}",name);
		/*
		 * We assume that every logged users in api manager has role
		 * PROVIDER.
		 */
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		roles.add(new SimpleGrantedAuthority("ROLE_PROVIDER"));
		//set authentication
		UserDetails userDetails = new User(name, "", true, true, true, true, roles);
		Authentication auth = new UsernamePasswordAuthenticationToken(
				userDetails,userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		request.getSession().setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
				SecurityContextHolder.getContext());
		
		return "index";
	}
	
	/**
	 * Redirect to OpenService login.
	 * 
	 * @return instance of {@link ResultData} with openservice login url
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public ResultData login(HttpServletResponse response) {
		logger.info("Login");
		final String redirectUrl = env.getProperty("redirectUrl");
		
		return new ResultData(redirectUrl, HttpServletResponse.SC_FORBIDDEN, 
				"Authentication on OpenService");
	}
	
	/**
	 * Callback rest service.
	 * After login in Openservice, this calls a rest service
	 * for setting authentication.
	 * 
	 * @param username : String, in request body
	 * @return String, uri of a rest service
	 */
	@RequestMapping(value = "/callback", method = RequestMethod.POST, 
			produces = "application/json")
	@ResponseBody
	public String callback(@RequestBody String username){
		logger.info("Api manager Callback");
		
		String url = env.getProperty("callbackUrl")+username;
		
		return url;
	}
	/**
	 * Try Request Handler class, with retrieving api url from request
	 * and saml 2.0 assertion from request body.
	 * 
	 * @param samlart : String, saml 2.0 assertion encoding in Base64
	 * @param request : instance of {@link HttpServletRequest}
	 * @param response : instance of {@link HttpServletResponse}
	 * @return instance of {@link ResultData} with data, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"ok", otherwise "Error".
	 * 			If exception is threw then its error is returned.
	 */
	@RequestMapping()
	@ResponseBody
	public ResultData requestHandl(@RequestBody(required=false) String samlart, 
			HttpServletRequest request, HttpServletResponse response){
		logger.info("-----------------------------------------------");
		logger.info("----------------START-------------------------------");
		
		//TODO Check Resource Verb
		//check samlart now is in body, but in headers?
		String method = request.getMethod();
		
		logger.info("samlart {}",samlart);
		try {
			RequestHandlerObject r = requestHandler.handleRequest(request,samlart);
			pdp.applyPoliciesBatch(r, method);

			logger.info("------------------END-----------------------------");
			logger.info("-----------------------------------------------");

			return new ResultData(r, HttpServletResponse.SC_OK, "ok");
			
			
		} catch (IllegalArgumentException i) {
			logger.info("Exception: {}", i.getMessage());
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND,
					i.getMessage());
		}catch(SecurityException s){
			logger.info("Security Exception: {}", s.getMessage());
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN,
					s.getMessage());
		}	

	}
	
}
