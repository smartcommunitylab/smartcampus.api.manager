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
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.VerifyAppKey;
import eu.trentorise.smartcampus.api.manager.model.util.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.SecurityManager;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;

/**
 * Controller that retrieves Policy data.
 * 
 * @author Giulia Canobbio
 *
 */
@Controller
@RequestMapping(value = "/api")
public class PolicyController {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);
	/**
	 * Instance of {@link SecurityManager}.
	 */
	@Autowired
	private SecurityManager smanager;
	
	/**
	 * Rest service that retrieves policy of an Api by policy and api id.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link ResultData} with api policy data having the given id, 
	 * 			status (OK, NOT FOUND or FORBIDDEN) and a string message : 
	 * 			"Policy data found" if it is ok, otherwise "There is no policy data for this api."
	 * 			or exception error message.
	 */
	@RequestMapping(value = "/{apiId}/policy/{policyId}", method = RequestMethod.GET, 
			produces="application/json")
	@ResponseBody
	public ResultData getPolicyApiById(@PathVariable String apiId, @PathVariable String policyId){
		logger.info("Retrieve Policy by id.");
		Policy p;
		try {
			p = smanager.getPolicyApiByPolicyId(apiId, policyId);
			
			if(p!=null){
				return new ResultData(p, HttpServletResponse.SC_OK, "Policy data found");
			}else{
				return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
						"There is no policy data for this api.");
			}
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
	 * 			"Add policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/spikeArrest", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody SpikeArrest p) {
		logger.info("Add Spike Arrest api policy.");
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
	 * 			"Add policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/quota", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody Quota p) {
		logger.info("Add Quota api policy.");
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
	 * 			"Add policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/ip", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody IPAccessControl p) {
		logger.info("Add IP Access Control api policy.");
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
	 * 			"Add policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/appkey", method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody VerifyAppKey p) {
		logger.info("Add Verify App Key api policy.");
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
	 * @param p : instance of {@link OAuth}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Add policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/add/{apiId}/policy/oauth", method = RequestMethod.POST, 
			consumes="application/json")
	@ResponseBody
	public ResultData addPolicy(@PathVariable String apiId, @RequestBody OAuth p) {
		logger.info("Add OAuth api policy.");
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
		logger.info("Update api policy IP Access Control.");
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
		logger.info("Update api policy Verify App Key.");
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
	 * @param p : instance of {@link OAuth}
	 * @return instance of {@link ResultData} with updated api data, status (OK, INTERNAL SERVER ERROR,
	 * 			BAD REQUEST or FORBIDDEN) and a string message : 
	 * 			"Updated policy Successfully" if it is ok, otherwise "Problem in updating data".
	 * 			If exception is threw then it is the exception message.
	 */
	@RequestMapping(value = "/update/{apiId}/policy/oauth", method = RequestMethod.PUT, 
			consumes="application/json")
	@ResponseBody
	public ResultData updatePolicy(@PathVariable String apiId, @RequestBody OAuth p) {
		logger.info("Update api policy OAuth.");
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
}
