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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.proxy.RequestHandler;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	@Autowired
	private RequestHandler requestHandler;
	
	/**
	 * Return "HelloWorld" string
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		logger.info("Hello World!");
		
		return "index";
	}
	
	@RequestMapping(value="/proxy", method = RequestMethod.POST)
	public ResultData requestHandler(@RequestBody String url){
		logger.info("proxy request handler");
		//requestHandler = new RequestHandler();
		try {
			String decodedurl = URLDecoder.decode(url, "UTF-8");
			logger.info("Decoded url: {}", decodedurl);
			requestHandler.handleRequest(null, decodedurl, null);
			
			logger.info("Api id: {}", requestHandler.getApiId());
			logger.info("Api resource ids: {}", requestHandler.getResourceIds());
			
			return new ResultData(null, HttpServletResponse.SC_OK, "ok");
			
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultData(null, HttpServletResponse.SC_OK, "ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultData(null, HttpServletResponse.SC_OK, "ok");
		}
	}
	
}
