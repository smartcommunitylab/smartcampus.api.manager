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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Controller that retrieves App data.
 * 
 * @author Giulia Canobbio
 *
 */
@Controller
@RequestMapping(value = "/api")
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
	 * Rest service that retrieves app of an Api by app and api id.
	 * 
	 * @param apiId : String
	 * @param appId : String
	 * @return instance of {@link ResultData} with api app data having the given id, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"App data found" if it is ok, otherwise "There is no app data for this api.".
	 */
	@RequestMapping(value = "/{apiId}/app/{appId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getResourceAppById(@PathVariable String apiId, @PathVariable String appId){
		logger.info("App by id.");
		App a = pmanager.getAppApiByAppId(apiId, appId);
		if(a!=null){
			return new ResultData(a, HttpServletResponse.SC_OK, "App data found");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"There is no app data for this api.");
		}
	}

}
