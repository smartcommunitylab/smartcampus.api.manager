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

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.api.manager.model.util.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.SecurityManager;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;

/**
 * Google Analytics Controller
 * It retrieves and save in mongo db tracking id of user and 
 * write data to its google analytics account.
 * 
 * @author Giulia Canobbio
 *
 */
@Controller
@RequestMapping(value = "/api/ga")
public class GAController {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(GAController.class);
	/**
	 * Instance of {@link SecurityManager}.
	 */
	@Autowired
	private SecurityManager smanager;
	
	/**
	 * REST service that retrieves tracking id from user
	 * and save it in db.
	 * 
	 * @param trackingID : String, in request body
	 * @return instance of {@link ResultData} without data, 
	 * 			status (OK or NOT FOUND) and a string message : 
	 * 			"User data saved correctly." if it is ok, otherwise 
	 * 			CustomAuthenticationException message.
	 */
	@RequestMapping(method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData getUserData(@RequestBody String trackingID){
		logger.info("User tracking ID");
		try{
			smanager.saveUserTrackingId(trackingID);
			return new ResultData(null, HttpServletResponse.SC_OK, "User data saved correctly.");
		}catch(CustomAuthenticationException c){
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, c.getMessage());
		}
	}

}
