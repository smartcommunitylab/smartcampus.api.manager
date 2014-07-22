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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Controller with CRUD methods on Api.
 * Add, update, delete and get api by owner id.
 * 
 * @author Giulia Canobbio
 *
 */
@Controller
@RequestMapping(value = "/api")
public class ApiController {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager pmanager;
	
	/**
	 * Rest service that adding an Api to database.
	 * 
	 * @param api : instance of {@link Api}
	 * @return string message : "Saved Successfully" if it is ok, otherwise "Problem in saving data".
	 * 				If exception is threw then it returns exception message.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData add(@RequestBody Api api) {
		logger.info("Add api to db.");
		try {
			Api savedApi = pmanager.addApi(api);
			if (savedApi != null) {
				return new ResultData(null, HttpServletResponse.SC_OK, "Saved successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in saving data.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData update(@RequestBody Api api) {
		logger.info("Update api to db.");
		try {
			Api updatedApi = pmanager.updateApi(api);
			if (updatedApi != null) {
				return new ResultData(null, HttpServletResponse.SC_OK, "Update successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	@RequestMapping(value = "/delete/{apiId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData delete(@PathVariable String apiId) {
		logger.info("Delete api to db.");
		
		pmanager.deleteApi(apiId);
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
			
	}
	
	@RequestMapping(value = "/{ownerId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getApiByOwnerId(@PathVariable String ownerId) {
		List<Api> apiList = pmanager.getApiByOwnerId(ownerId);

		if(apiList!=null){
			return new ResultData(apiList, HttpServletResponse.SC_OK, "All data.");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, "There is no api data for this owner.");
		}
		
	}
}
