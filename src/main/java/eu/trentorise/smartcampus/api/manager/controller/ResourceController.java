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

import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Controller that retrieves Resource data.
 * 
 * @author Giulia Canobbio
 *
 */
@Controller
@RequestMapping(value = "/api")
public class ResourceController {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager pmanager;
	
	/**
	 * Rest service that retrieves resource of an Api by resource and api id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link ResultData} with api resource data having the given id, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "There is no resource data for this api.".
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getResourceApiById(@PathVariable String apiId, @PathVariable String resourceId){
		logger.info("Resource by id.");
		Resource r = pmanager.getResourceApiByResourceId(apiId, resourceId);
		if(r!=null){
			return new ResultData(r, HttpServletResponse.SC_OK, "Resource data found");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"There is no resource data for this api.");
		}
	}

}
