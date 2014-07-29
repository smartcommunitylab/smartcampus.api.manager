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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
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
	@RequestMapping(value = "/{apiId}/resource/{resourceId}", method = RequestMethod.GET, 
			produces="application/json")
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
	
	/**
	 * Rest service that retrieves policy resource of an Api by api, resource and policy ids.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link ResultData} with policy resource data having the given id, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"Policy Resource data found" if it is ok, 
	 * 			otherwise "There is no policy resource data for this api.".
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/policy/{policyId}", method = RequestMethod.GET, 
			produces="application/json")
	@ResponseBody
	public ResultData getResourcePolicyById(@PathVariable String apiId, @PathVariable String resourceId,
			@PathVariable String policyId){
		logger.info("Policy Resource by id.");
		Policy p = pmanager.getPolicyResourceApiByResourceId(apiId, resourceId, policyId);
		if(p!=null){
			return new ResultData(p, HttpServletResponse.SC_OK, "Policy Resource data found");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"There is no policy resource data for this api.");
		}
	}
	
	/**
	 * Rest that add a policy to resource api.
	 * NOT NEEDED
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Policy}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "Problem in saving policy to resource api.".
	 */
	/*@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy", method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody Policy p){
		logger.info("Add policy to resource.");
		Resource r = pmanager.addPolicyResourceApi(apiId, resourceId, p);
		if(r!=null){
			return new ResultData(r, HttpServletResponse.SC_OK, "Resource data found");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem in saving policy to resource api.");
		}
	}
	*/
	/**
	 * Rest that add a spike arrest policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK, BAD REQUEST and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "Problem in saving policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy/spikeArrest", 
			method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody SpikeArrest p){
		logger.info("Add policy to resource.");
		try {
			Resource r = pmanager.addPolicyResourceApi(apiId, resourceId, p);
			if (r != null) {
				return new ResultData(r, HttpServletResponse.SC_OK,
						"Resource policy added successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND,
						"Problem in saving policy to resource api.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST,
					i.getMessage());
		}
	}
	
	/**
	 * Rest that add a quota policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK, BAD REQUEST and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "Problem in saving policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy/quota", 
			method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody Quota p){
		logger.info("Add policy to resource.");
		try {
			Resource r = pmanager.addPolicyResourceApi(apiId, resourceId, p);
			if (r != null) {
				return new ResultData(r, HttpServletResponse.SC_OK,
						"Resource policy added successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND,
						"Problem in saving policy to resource api.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST,
					i.getMessage());
		}
	}
	
	/**
	 * Rest that update a policy to resource api.
	 * NOT NEEDED
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Policy}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "Problem in updating policy to resource api.".
	 */
	/*@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy", method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody Policy p){
		logger.info("Update policy to resource.");
		Resource r = pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		if(r!=null){
			return new ResultData(r, HttpServletResponse.SC_OK, "Resource data found");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem in updating policy to resource api.");
		}
	}
	*/
	/**
	 * Rest that update a spike arrest policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK, BAD REQUEST and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "Problem in updating policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy/spikeArrest", 
			method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody SpikeArrest p){
		logger.info("Update policy to resource.");
		try {
			Resource r = pmanager.updatePolicyResourceApi(apiId, resourceId, p);
			if (r != null) {
				return new ResultData(r, HttpServletResponse.SC_OK,
						"Resource policy updated successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND,
						"Problem in updating policy to resource api.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST,
					i.getMessage());
		}
	}
	
	/**
	 * Rest that update a quota policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK, BAD REQUEST and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "Problem in updating policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy/quota", 
			method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody Quota p){
		logger.info("Update policy to resource.");
		Resource r = pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		try {
			if (r != null) {
				return new ResultData(r, HttpServletResponse.SC_OK,
						"Resource policy updated successfully.");
			} else {
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND,
						"Problem in updating policy to resource api.");
			}
		} catch (IllegalArgumentException i) {
			return new ResultData(null, HttpServletResponse.SC_BAD_REQUEST,
					i.getMessage());
		}
	}
	
	/**
	 * Rest that deletes a policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link ResultData} with resource data without deleted policy, 
	 * 			status (OK and NOT FOUND) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "Problem in updating policy to resource api.".
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/delete/{policyId}", 
			method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@PathVariable String policyId){
		logger.info("Delete policy from resource.");
		pmanager.deletePolicyResourceApi(apiId, resourceId, policyId);
		
		return new ResultData(null, HttpServletResponse.SC_OK, "Resource delete successfully");
		
	}

}
