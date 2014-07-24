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
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
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
	 * Rest service that retrieving api data by id.
	 * 
	 * @param apiId : String
	 * @return instance of {@link ResultData} with api data having the given id, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"Api data found" if it is ok, otherwise "There is no api data with this id.".
	 */
	@RequestMapping(value = "/id/{apiId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getApiById(@PathVariable String apiId){
		logger.info("Api by id.");
		Api api = pmanager.getApiById(apiId);
		if(api!=null){
			return new ResultData(api, HttpServletResponse.SC_OK, "Api data found");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, "There is no api data with this id.");
		}
	}
	
	/**
	 * Rest service that retrieving api data having a specific owner id.
	 * 
	 * @param ownerId : String, path variable
	 * @return instance of {@link ResultData} with api data having the given owner id, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"All data" if it is ok, otherwise "There is no api data for this owner.".
	 */
	@RequestMapping(value = "/{ownerId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getApiByOwnerId(@PathVariable String ownerId) {
		logger.info("List api by owner id.");
		List<Api> apiList = pmanager.getApiByOwnerId(ownerId);

		if(apiList!=null && apiList.size()>0){
			return new ResultData(apiList, HttpServletResponse.SC_OK, "All data.");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, "There is no api data for this owner.");
		}
		
	}
	
	/**
	 * Rest service that adding an Api to database.
	 * 
	 * @param api : instance of {@link Api}
	 * @return instance of {@link ResultData} with api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Saved Successfully" if it is ok, otherwise "Problem in saving data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData add(@RequestBody Api api) {
		logger.info("Add api to db.");
		try {
			Api savedApi = pmanager.addApi(api);
			if (savedApi != null) {
				return new ResultData(savedApi, HttpServletResponse.SC_OK, "Saved successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in saving data.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	/**
	 * Rest service that updating a resource api.
	 * 
	 * @param apiId : String
	 * @param resource : instance of {@link Resource}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated resource Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */ 
	@RequestMapping(value = "/add/{apiId}/resource", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addResource(@PathVariable String apiId, @RequestBody Resource resource) {
		logger.info("Update api resource.");
		try{
			Resource updateApiR = pmanager.addResourceApi(apiId,resource);
			if(updateApiR!=null){
				return new ResultData(updateApiR, HttpServletResponse.SC_OK, "Add resource successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	/**
	 * Rest service that updating an app api.
	 * 
	 * @param apiId : String
	 * @param app : instance of {@link App}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated app Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/app", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addApp(@PathVariable String apiId, @RequestBody App app) {
		logger.info("Update api app.");
		try{
			App updateApiA = pmanager.addAppApi(apiId, app);
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
	 * Rest service that updating a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Policy}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody Policy p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = pmanager.addPolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Add policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updating a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/spikeArrest", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody SpikeArrest p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = pmanager.addPolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Add policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updating a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/quota", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody Quota p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = pmanager.addPolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Add policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updating an Api in database.
	 * 
	 * @param api : instance of {@link Api}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData update(@RequestBody Api api) {
		logger.info("Update api to db.");
		try {
			Api updatedApi = pmanager.updateApiParameter(api);
			if (updatedApi != null) {
				return new ResultData(updatedApi, HttpServletResponse.SC_OK, "Update successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	/**
	 * Rest service that updating a resource api.
	 * 
	 * @param apiId : String
	 * @param resource : instance of {@link Resource}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated resource Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */ 
	@RequestMapping(value = "/update/{apiId}/resource", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData updateResource(@PathVariable String apiId, @RequestBody Resource resource) {
		logger.info("Update api resource.");
		try{
			Resource updateApiR = pmanager.updateResourceApi(apiId,resource);
			if(updateApiR!=null){
				return new ResultData(updateApiR, HttpServletResponse.SC_OK, "Update resource successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	/**
	 * Rest service that updating an app api.
	 * 
	 * @param apiId : String
	 * @param app : instance of {@link App}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated app Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/app", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData updateApp(@PathVariable String apiId, @RequestBody App app) {
		logger.info("Update api app.");
		try{
			App updateApiA = pmanager.updateAppApi(apiId, app);
			if(updateApiA!=null){
				return new ResultData(updateApiA, HttpServletResponse.SC_OK, "Update app successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
	}
	
	/**
	 * Rest service that updating a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Policy}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody Policy p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = pmanager.updatePolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Update policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updating a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy/spikeArrest", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody SpikeArrest p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = pmanager.updatePolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Update policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updating a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR and
	 * 			BAD REQUEST) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy/quota", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody Quota p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = pmanager.updatePolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Update policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		}
		
	}
	
	/**
	 * Rest service that deleting an Api from database by passing its id.
	 * 
	 * @param apiId : String
	 * @return instance of {@link ResultData} with status (OK) and a string message : 
	 * 			"Delete done!".
	 */
	@RequestMapping(value = "/delete/{apiId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData delete(@PathVariable String apiId) {
		logger.info("Delete api to db.");
		
		pmanager.deleteApi(apiId);
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");	
	}
	
	/**
	 * Rest service that deleting a Resource Api from database by passing its id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link ResultData} with status (OK) and a string message : 
	 * 			"Delete done!".
	 */
	@RequestMapping(value = "/delete/{apiId}/resource/{resourceId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteResource(@PathVariable String apiId, @PathVariable String resourceId){
		logger.info("Delete api resource.");
		
		pmanager.deleteResourceApi(apiId,resourceId);
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
	}
	
	/**
	 * Rest service that deleting an App Api from database by passing its id.
	 * 
	 * @param apiId : String
	 * @param appId : String
	 * @return instance of {@link ResultData} with status (OK) and a string message : 
	 * 			"Delete done!".
	 */
	@RequestMapping(value = "/delete/{apiId}/app/{appId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteApp(@PathVariable String apiId, @PathVariable String appId){
		logger.info("Delete api resource.");
		
		pmanager.deleteAppApi(apiId,appId);
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
	}
	
	/**
	 * Rest service that deleting an Policy Api from database by passing its id.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link ResultData} with status (OK) and a string message : 
	 * 			"Delete done!".
	 */
	@RequestMapping(value = "/delete/{apiId}/policy/{policyId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deletePolicy(@PathVariable String apiId, @PathVariable String policyId){
		logger.info("Delete api resource.");
		
		pmanager.deletePolicyApi(apiId,policyId);
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
	}
}
