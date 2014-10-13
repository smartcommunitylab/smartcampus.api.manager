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

import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.OAuth;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.ResultData;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.VerifyAppKey;
import eu.trentorise.smartcampus.api.manager.persistence.SecurityManager;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;

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
	 * Instance of {@link SecurityManager}.
	 */
	@Autowired
	private SecurityManager manager;
	
	/**
	 * Rest service that retrieves resource of an Api by resource and api id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link ResultData} with api resource data having the given id, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource data found" if it is ok, otherwise "There is no resource data for this api."
	 * 			or exception error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}", method = RequestMethod.GET, 
			produces="application/json")
	@ResponseBody
	public ResultData getResourceApiById(@PathVariable String apiId, @PathVariable String resourceId){
		logger.info("Retrieve Resource by id.");
		try {
			Resource r = manager.getResourceApiByResourceId(apiId, resourceId);
			
			if(r!=null){
				return new ResultData(r, HttpServletResponse.SC_OK, "Resource data found");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no resource data for this api.");
			}
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest service that retrieves policy resource of an Api by api, resource and policy ids.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link ResultData} with policy resource data having the given id, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Policy Resource data found" if it is ok, 
	 * 			otherwise "There is no policy resource data for this api."
	 * 			or exception error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/policy/{policyId}", method = RequestMethod.GET, 
			produces="application/json")
	@ResponseBody
	public ResultData getResourcePolicyById(@PathVariable String apiId, @PathVariable String resourceId,
			@PathVariable String policyId){
		logger.info("Retrieve Policy Resource by id.");
		try {
			Policy p = manager.getPolicyResourceApiByResourceId(apiId, resourceId, policyId);
			if(p!=null){
				return new ResultData(p, HttpServletResponse.SC_OK, "Policy Resource data found");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no policy resource data for this api.");
			}
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest service that adds a resource api.
	 * 
	 * @param apiId : String
	 * @param resource : instance of {@link Resource}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Add resource successfully." if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */ 
	@RequestMapping(value = "/add/{apiId}/resource", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addResource(@PathVariable String apiId, @RequestBody Resource resource) {
		logger.info("Add api resource.");
		try{
			Resource updateApiR = manager.addResourceApi(apiId,resource);
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
	 * Rest that add a spike arrest policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy added successfully." if it is ok, otherwise "Problem in saving policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy/spikeArrest", 
			method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody SpikeArrest p){
		logger.info("Add policy Spike Arrest to resource.");
		try {
			Resource r = manager.addPolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that add a quota policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy added successfully." if it is ok, 
	 * 			otherwise "Problem in saving policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy/quota", 
			method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody Quota p){
		logger.info("Add policy Quota to resource.");
		try {
			Resource r = manager.addPolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that add an ip access control policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy added successfully." if it is ok, 
	 * 			otherwise "Problem in saving policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy/ip", 
			method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody IPAccessControl p){
		logger.info("Add policy IP Access Control to resource.");
		try {
			Resource r = manager.addPolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that add a verify app key policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy added successfully." if it is ok, 
	 * 			otherwise "Problem in saving policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy/appkey", 
			method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody VerifyAppKey p){
		logger.info("Add policy Verify App Key to resource.");
		try {
			Resource r = manager.addPolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that add an oauth policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link OAuth}
	 * @return instance of {@link ResultData} with resource data having the new policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy added successfully." if it is ok, 
	 * 			otherwise "Problem in saving policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/add/policy/oauth", 
			method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody OAuth p){
		logger.info("Add policy OAuth to resource.");
		try {
			Resource r = manager.addPolicyResourceApi(apiId, resourceId, p);
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
	 * 			"Updated resource successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */ 
	@RequestMapping(value = "/update/{apiId}/resource", method = RequestMethod.PUT, consumes="application/json")
	@ResponseBody
	public ResultData updateResource(@PathVariable String apiId, @RequestBody Resource resource) {
		logger.info("Update api resource.");
		try{
			Resource updateApiR = manager.updateResourceApi(apiId,resource);
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
	 * Rest that update a spike arrest policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy updated successfully." if it is ok, 
	 * 			otherwise "Problem in updating policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy/spikeArrest", 
			method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody SpikeArrest p){
		logger.info("Update policy Spike Arrest to resource.");
		try {
			Resource r = manager.updatePolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that update a quota policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy updated successfully."" if it is ok, 
	 * 			otherwise "Problem in updating policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy/quota", 
			method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody Quota p){
		logger.info("Update policy Quota to resource.");
		try{
			Resource r = manager.updatePolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that update an ip access control policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy updated successfully." if it is ok, 
	 * 			otherwise "Problem in updating policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy/ip", 
			method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody IPAccessControl p){
		logger.info("Update policy IP Access Control to resource.");
		try{
			Resource r = manager.updatePolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that update a verify app key policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy updated successfully."" if it is ok, 
	 * 			otherwise "Problem in updating policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy/appkey", 
			method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody VerifyAppKey p){
		logger.info("Update policy Verify App Key to resource.");
		try{
			Resource r = manager.updatePolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	/**
	 * Rest that update an oauth policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link OAuth}
	 * @return instance of {@link ResultData} with resource data having the updated policy, 
	 * 			status (OK, BAD REQUEST, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Resource policy updated successfully."" if it is ok, 
	 * 			otherwise "Problem in updating policy to resource api."
	 * 			or if exception is thrown, the error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/update/policy/oauth", 
			method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updateResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@RequestBody OAuth p){
		logger.info("Update policy OAuth to resource.");
		try{
			Resource r = manager.updatePolicyResourceApi(apiId, resourceId, p);
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
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
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
			manager.deleteResourceApi(apiId,resourceId);
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		return new ResultData(null, HttpServletResponse.SC_OK, "Delete done!");
	}
	
	/**
	 * Rest that deletes a policy to resource api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link ResultData} with resource data without deleted policy, 
	 * 			status (OK or FORBIDDEN) and a string message : 
	 * 			"Delete policy from resource." if it is ok, otherwise exception error message.
	 */
	@RequestMapping(value = "/{apiId}/resource/{resourceId}/delete/{policyId}", 
			method = RequestMethod.DELETE)
	@ResponseBody
	public ResultData deleteResourcePolicy(@PathVariable String apiId, @PathVariable String resourceId,
			@PathVariable String policyId){
		logger.info("Delete policy from resource.");
		try {
			manager.deletePolicyResourceApi(apiId, resourceId, policyId);
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
		
		return new ResultData(null, HttpServletResponse.SC_OK, "Resource delete successfully");
		
	}

}
