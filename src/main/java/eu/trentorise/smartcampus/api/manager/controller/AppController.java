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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.manager.persistence.SecurityManager;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;

/**
 * Controller that retrieves App data.
 * 
 * @author Giulia Canobbio
 *
 */
@Controller
@RequestMapping(value = "/api/app")
public class AppController {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(AppController.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager pmanager;
	/**
	 * Instance of {@link SecurityManager}.
	 */
	@Autowired
	private SecurityManager smanager;
	
	/**
	 * Rest service that retrieves apps saved in db for the current user.
	 * 
	 * @return instance of {@link ResultData} with apps data, 
	 * 			status (OK or NOT FOUND) and a string message : 
	 * 			"App data found" if it is ok, otherwise "There is no app data for this api.".
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getAppList(){
		logger.info("App list.");
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		List<App> alist = pmanager.listApp(user);
		if(alist!=null){
			return new ResultData(alist, HttpServletResponse.SC_OK, "App data found");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"There is no app data for this api.");
		}
	}
	
	/**
	 * Rest service that retrieves app by id.
	 * 
	 * @param appId : String
	 * @return instance of {@link ResultData} with api app data having the given id, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"App data found" if it is ok, otherwise "There is no app data.".
	 * 			If security exception is threw then exception error message is returned.
	 */
	@RequestMapping(value = "/{appId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getResourceAppById(@PathVariable String appId){
		logger.info("App by id.");
		try {
			App a = smanager.getAppById(appId);
			if (a != null) {
				return new ResultData(a, HttpServletResponse.SC_OK,
						"App data found");
			} else {
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND,
						"There is no app data.");
			}
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN,
					e.getMessage());
		}
	}
	
	/**
	 * Add a new app.
	 * 
	 * @param app : instance of {@link App}
	 * @return instance of {@link ResultData} with new app data, status (OK, INTERNAL SERVER ERROR 
	 * 			or BAD REQUEST) and a string message : 
	 * 			"Add app successfully." if it is ok, otherwise "Problem in adding data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addApp(@RequestBody App app) {
		logger.info("Add app.");
		try{
			App updateApiA = pmanager.addApp(app);
			if(updateApiA!=null){
				return new ResultData(updateApiA, HttpServletResponse.SC_OK, "Add app successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	/**
	 * Update a new app.
	 * 
	 * @param app : instance of {@link App}
	 * @return instance of {@link ResultData} with updated app data, status (OK, INTERNAL SERVER ERROR,
	 * 			 BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Update app successfully." if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData updateApp(@RequestBody App app) {
		logger.info("Update app.");
		try{
			App updateApiA = smanager.updateApp(app);
			if(updateApiA!=null){
				return new ResultData(updateApiA, HttpServletResponse.SC_OK, "Update app successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN,
					e.getMessage());
		}
	}
	
	/**
	 * Delete an app from db.
	 * 
	 * @param appId : String
	 * @return instance of {@link ResultData} without data, status (OK or FORBIDDEN) and a string message : 
	 * 			"Delete done." or security exception error message.
	 */
	@RequestMapping(value = "/delete/{appId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteApp(@PathVariable String appId){
		logger.info("Delete app.");
		try{
			smanager.deleteApp(appId);
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN,
				e.getMessage());
		}
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
	}
	
	/**
	 * Update api data of an app.
	 * 
	 * @param app : instance of {@link App}
	 * @return instance of {@link ResultData} with updated app data, status (OK, INTERNAL SERVER ERROR or
	 * 			FORBIDDEN) 
	 * 			and a string message : "Update app api data successfully." 
	 * 			if it is ok, otherwise "Problem in updating data".
	 * 			If security exception is threw then exception error message is returned.
	 */
	@RequestMapping(value = "/update/apidata", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData updateAppApiData(@RequestBody App app) {
		logger.info("Update app api data.");
		try {
			App updateApiA = smanager.updateAppApiData(app);
			if (updateApiA != null) {
				return new ResultData(updateApiA, HttpServletResponse.SC_OK,
						"Update app api data successfully.");
			} else {
				return new ResultData(null,
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Problem in updating data.");
			}
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN,
					e.getMessage());
		}

	}
	
	/**
	 * Delete api data from app.
	 * 
	 * @param appId : String
	 * @param apiId : String
	 * @return instance of {@link ResultData} without data, status (OK or FORBIDDEN) and a string message : 
	 * 			"Delete api data from app done!" or security error message.
	 */
	@RequestMapping(value = "/delete/{appId}/api/{apiId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteApiData(@PathVariable String appId, @PathVariable String apiId){
		logger.info("Delete api data from app.");
		try{
			smanager.deleteAppApiData(appId, apiId);
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete api data from app done!");
	}

}
