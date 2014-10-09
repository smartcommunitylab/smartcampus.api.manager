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
import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.Status;
import eu.trentorise.smartcampus.api.manager.model.VerifyAppKey;
import eu.trentorise.smartcampus.api.manager.persistence.SecurityManager;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;

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
	 * Instance of {@link SecurityManager}
	 */
	@Autowired
	private SecurityManager smanager;
	
	/**
	 * Rest service that retrieves api data by id.
	 * 
	 * @param apiId : String
	 * @return instance of {@link ResultData} with api data having the given id, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Api data found" if it is ok, "There is no api data with this id."
	 * 			otherwise exception CustomAuthentication message.
	 */
	@RequestMapping(value = "/id/{apiId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getApiById(@PathVariable String apiId){
		logger.info("Api by id.");
		Api api;
		try {
			api = smanager.getApiById(apiId);
			
			if(api!=null){
				return new ResultData(api, HttpServletResponse.SC_OK, "Api data found");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no api data with this id.");
			}
			
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest service that retrieves api name.
	 * 
	 * @param apiId : String
	 * @return instance of {@link ResultData} with api name having the given id, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Api name found" if it is ok, "There is no api data with this id."
	 * 			otherwise "User is not allowed".
	 */
	@RequestMapping(value = "/name/{apiId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getApiByName(@PathVariable String apiId){
		logger.info("Api name.");
		
		try {
			String api = smanager.getApiByName(apiId);
			if(api!=null){
				return new ResultData(api, HttpServletResponse.SC_OK, "Api name found");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no api with this id.");
			}
			
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, "User is not allowed");
		}
	}
	
	/**
	 * Rest service that retrieves api data having a specific owner id.
	 * 
	 * @param ownerId : String, path variable
	 * @return instance of {@link ResultData} with api data having the given owner id, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"All data" if it is ok, "There is no api data for this owner."
	 * 			otherwise exception CustomAuthentication message.
	 */
	@RequestMapping(value = "/ownerId", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ResultData getApiByOwnerId() {
		logger.info("List api by owner id.");
	
		try {
			List<Api> apiList = smanager.getApiByOwnerId();
			
			if(apiList!=null && apiList.size()>0){
				return new ResultData(apiList, HttpServletResponse.SC_OK, "All data.");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no api data for this owner.");
			}
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that adds an Api to database.
	 * 
	 * @param api : instance of {@link Api}
	 * @return instance of {@link ResultData} with api data, status (OK, INTERNAL SERVER ERROR or
	 * 			BAD REQUEST) and a string message : 
	 * 			"Saved Successfully" if it is ok, otherwise "Problem in saving data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData add(@RequestBody Api api) {
		logger.info("Add api to db.");		
		try {
			Api savedApi = smanager.addApi(api);
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
	 * Rest service that adds a resource api.
	 * 
	 * @param apiId : String
	 * @param resource : instance of {@link Resource}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated resource Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */ 
	@RequestMapping(value = "/add/{apiId}/resource", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addResource(@PathVariable String apiId, @RequestBody Resource resource) {
		logger.info("Update api resource.");
		try{
			Resource updateApiR = smanager.addResourceApi(apiId,resource);
			if(updateApiR!=null){
				return new ResultData(updateApiR, HttpServletResponse.SC_OK, "Add resource successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest service that adds a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/spikeArrest", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody SpikeArrest p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = smanager.addPolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Add policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that adds a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/quota", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody Quota p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = smanager.addPolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Add policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that adds a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/ip", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody IPAccessControl p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = smanager.addPolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Add policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that adds a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/appkey", method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody VerifyAppKey p) {
		logger.info("Update api policy.");
		try{
			Policy updateApiP = smanager.addPolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Add policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in adding data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updates an Api in database.
	 * PROBLEM TODO: when trying to update Api data, and list policy is populated, it
	 * returns a bad request error.
	 * 
	 * @param api : instance of {@link Api}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData update(@RequestBody Api api) {
		logger.info("Update api to db.");
		try {
			Api updatedApi = smanager.updateApiParameter(api);
			if (updatedApi != null) {
				return new ResultData(updatedApi, HttpServletResponse.SC_OK, "Update successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest service that updates a resource api.
	 * Policy parameter must be set to null, because this method
	 * does not update policy, only uri and verb parameters, otherwise
	 * it returns BAD REQUEST. 
	 * 
	 * @param apiId : String
	 * @param resource : instance of {@link Resource}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated resource Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */ 
	@RequestMapping(value = "/update/{apiId}/resource", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData updateResource(@PathVariable String apiId, @RequestBody Resource resource) {
		logger.info("Update api resource.");
		try{
			Resource updateApiR = smanager.updateResourceApi(apiId,resource);
			if(updateApiR!=null){
				return new ResultData(updateApiR, HttpServletResponse.SC_OK, "Update resource successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest service that updates a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy/spikeArrest", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody SpikeArrest p) {
		logger.info("Update api policy spike arrest.");
		try{
			Policy updateApiP = smanager.updatePolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Update policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updates a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy/quota", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody Quota p) {
		logger.info("Update api policy quota.");
		try{
			Policy updateApiP = smanager.updatePolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Update policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updates a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy/ip", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody IPAccessControl p) {
		logger.info("Update api policy quota.");
		try{
			Policy updateApiP = smanager.updatePolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Update policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that updates a policy api.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy/appkey", method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody VerifyAppKey p) {
		logger.info("Update api policy quota.");
		try{
			Policy updateApiP = smanager.updatePolicyApi(apiId, p);
			if(updateApiP!=null){
				return new ResultData(updateApiP, HttpServletResponse.SC_OK, "Update policy successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"Problem in updating data.");
			}
		}catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Rest service that deletes an Api from database by passing its id.
	 * 
	 * @param apiId : String
	 * @return instance of {@link ResultData} with status (OK or FORBIDDEN) and a string message : 
	 * 			"Delete done!" or exception error message.
	 */
	@RequestMapping(value = "/delete/{apiId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData delete(@PathVariable String apiId) {
		logger.info("Delete api to db.");
		try{
			smanager.deleteApi(apiId);
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");	
	}
	
	/**
	 * Rest service that deletes a Resource Api from database by passing its id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link ResultData} with status (OK or FORBIDDEN) and a string message : 
	 * 			"Delete done!" or exception CustomAuthentication message.
	 */
	@RequestMapping(value = "/delete/{apiId}/resource/{resourceId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteResource(@PathVariable String apiId, @PathVariable String resourceId){
		logger.info("Delete api resource.");
		try {
			smanager.deleteResourceApi(apiId,resourceId);
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
	}
	
	/**
	 * Rest service that deletes an Policy Api from database by passing its id.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link ResultData} with status (OK or FORBIDDEN) and a string message : 
	 * 			"Delete done!" otherwise exception error message.
	 */
	@RequestMapping(value = "/delete/{apiId}/policy/{policyId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deletePolicy(@PathVariable String apiId, @PathVariable String policyId){
		logger.info("Delete api resource.");
		
		try {
			smanager.deletePolicyApi(apiId,policyId);
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
	}
	
	/**
	 * Rest service that retrieves an api status searching by status name.
	 * 
	 * @param apiId : String
	 * @param statusName : String
	 * @return instance of {@link ResultData} with data, status (OK, NOT FOUND or FORBIDDEN) and 
	 * 			a string message : "Status api found." or if data is null 
	 * 			"There is no status with this name." otherwise exception error message.
	 */
	@RequestMapping(value = "/{apiId}/status/{statusName}", method = RequestMethod.GET, 
			produces="application/json")
	@ResponseBody
	public ResultData getApiStatusByStatusName(@PathVariable String apiId, 
			@PathVariable String statusName) {
		logger.info("List api by owner id.");
		
		try {
			Status s = smanager.getApiStatusByStatusName(apiId, statusName);
			
			if(s!=null){
				return new ResultData(s, HttpServletResponse.SC_OK, "Status api found.");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no status with this name.");
			}
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}

		
	}
	
	/**
	 * Rest service that retrieves list of api status.
	 * 
	 * @param apiId : String
	 * @return instance of {@link ResultData} with data, status (OK, NOT FOUND or FORBIDDEN) and 
	 * 			a string message : "Status list found." or if data is null "There is no status for this api."
	 * 			otherwise exception error message.
	 */
	@RequestMapping(value = "/{apiId}/status", method = RequestMethod.GET, 
			produces="application/json")
	@ResponseBody
	public ResultData getApiStatus(@PathVariable String apiId) {
		logger.info("List api by owner id.");
		List<Status> s;
		try {
			s = smanager.getApiStatus(apiId);
			
			if(s!=null && s.size()>0){
				return new ResultData(s, HttpServletResponse.SC_OK, "Status list found.");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no status for this api.");
			}
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Creates a new status entry for an api.
	 * 
	 * @param apiId : String
	 * @param s : instance of {@link Status}
	 * @return instance of {@link ResultData} with api status data, status (OK, BAD REQUEST or FORBIDDEN) 
	 * 			and a string message : 
	 * 			"Status saved successfully." if it is ok.
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/status", method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addApiStatus(@PathVariable String apiId, @RequestBody Status s){
		logger.info("Add api status.");
		try{
			List<Status> slist = smanager.addStatusApi(apiId, s);
			return new ResultData(slist, HttpServletResponse.SC_OK, "Status saved successfully.");
		}catch(IllegalArgumentException i){
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Updates a status of an api.
	 * 
	 * @param apiId : String
	 * @param s : instance of {@link Status}
	 * @return instance of {@link ResultData} with api status data, status (OK, BAD REQUEST or FORBIDDEN) 
	 * 			and a string message : 
	 * 			"Status updated successfully." if it is ok.
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/status", method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateStatus(@PathVariable String apiId, @RequestBody Status s){
		logger.info("Update api status.");
		try{
			List<Status> slist = smanager.updateStatusApi(apiId, s);
			return new ResultData(slist, HttpServletResponse.SC_OK, "Status updated successfully.");
		}catch(IllegalArgumentException i){
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
	}
	
	/**
	 * Deletes a status from an api.
	 * 
	 * @param apiId : String
	 * @param s : instance of {@link Status}
	 * @return instance of {@link ResultData} with status (OK, BAD REQUEST or FORBIDDEN) 
	 * 			and a string message : 
	 * 			"Delete done!" if it is ok.
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/delete/{apiId}/status/{statusName}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteStatus(@PathVariable String apiId, @PathVariable String statusName){
		logger.info("Delete api status.");
		try{
			smanager.deleteStatusApi(apiId, statusName);
			return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
		}catch(IllegalArgumentException i){
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST, i.getMessage());
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
}
