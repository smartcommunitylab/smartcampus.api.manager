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
package eu.trentorise.smartcampus.api.manager.persistence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.OAuth;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SAML;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.Status;
import eu.trentorise.smartcampus.api.manager.model.VerifyAppKey;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;

/**
 * Class that checks current user and owner of Api or App
 * data.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
public class SecurityManager {
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager pmanager;
	/**
	 * Instance of {@link PermissionManager}.
	 */
	@Autowired
	private PermissionManager security;
	
	/**
	 * This function set owner of api retrieving it from security context
	 * and then it add new api.
	 * 
	 * @param api : instance {@link Api}
	 * @return new instance of {@link Api}
	 */
	public Api addApi(Api api) {
		//owner id
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		api.setOwnerId(user);
		
		Api savedApi = pmanager.addApi(api);
		return savedApi;
	}

	/**
	 * This function checks user permission before
	 * retrieving api data by its id.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @return instance of {@link Api}
	 * @throws CustomAuthenticationException
	 */
	public Api getApiById(String apiId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return api;
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * retrieving api name.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @return name of api, String
	 * @throws CustomAuthenticationException
	 */
	public String getApiByName(String apiId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if (api != null) {
			String owner = api.getOwnerId();
			if (security.canUserDoThisOperation(owner)) {
				return api.getName();
			} else {
				throw new CustomAuthenticationException("You are not allowed");
			}
		}
		return null;
	}
	
	/**
	 * This function checks user permission before
	 * retrieving api data by its owner id.
	 * The owner id is name of the current user.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @return list of {@link Api} instances
	 * @throws CustomAuthenticationException
	 */
	public List<Api> getApiByOwnerId() throws CustomAuthenticationException{
		String ownerId = security.getUsername();
		List<Api> apilist = pmanager.getApiByOwnerId(ownerId);
		//order
		Collections.sort(apilist, new Comparator<Api>() {

			@Override
			public int compare(Api o1, Api o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		return apilist;
	}
	
	/**
	 * This function checks user permission before
	 * adding a resource to an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resource : instance of {@link Resource}
	 * @return instance of {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource addResourceApi(String apiId, Resource resource) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addResourceApi(apiId, resource);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}

	/**
	 * This function checks user permission before
	 * adding a policy to api.
	 * Policy must be a Spike Arrest.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy addPolicyApi(String apiId, SpikeArrest p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy spike arrest is already in
			List<Policy> listp = api.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof SpikeArrest){
						throw new IllegalArgumentException(
								"A policy spike arrest already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a policy to api.
	 * Policy must be a Quota.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * It throws an exception if a policy quota already exists for the resource.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy addPolicyApi(String apiId, Quota p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy quota is already in
			List<Policy> listp = api.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof Quota){
						throw new IllegalArgumentException(
								"A policy quota already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a policy to api.
	 * Policy must be an IP Access Control.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy addPolicyApi(String apiId, IPAccessControl p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy ip access control is already in
			List<Policy> listp = api.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof IPAccessControl){
						throw new IllegalArgumentException(
								"A policy ip access control already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a policy to api.
	 * Policy must be an Verify App Key.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy addPolicyApi(String apiId, VerifyAppKey p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy ip access control is already in
			List<Policy> listp = api.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof VerifyAppKey){
						throw new IllegalArgumentException(
								"A policy verify app key already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a policy to api.
	 * Policy must be an OAuth.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link OAuth}
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy addPolicyApi(String apiId, OAuth p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy oauth is already in
			List<Policy> listp = api.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof OAuth){
						throw new IllegalArgumentException(
								"A policy oauth already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a policy to api.
	 * Policy must be a SAML.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SAML}
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy addPolicyApi(String apiId, SAML p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy SAML is already in
			List<Policy> listp = api.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof SAML){
						throw new IllegalArgumentException(
								"A policy SAML already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating api data.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param api : instance of {@link Api}
	 * @return instance of updated {@link Api}
	 * @throws CustomAuthenticationException
	 */
	public Api updateApiParameter(Api api) throws CustomAuthenticationException{
		if(api.getId()!=null && api.getId().equalsIgnoreCase("")){
			Api savedApi = pmanager.getApiById(api.getId());
			if(security.canUserDoThisOperation(savedApi.getOwnerId())){
				return pmanager.updateApiParameter(api);
			}
			else {
				throw new CustomAuthenticationException("You are not allowed");
			}
		}
		else throw new IllegalArgumentException("Api with undefined id does not exist.");
	}
	
	/**
	 * This function checks user permission before
	 * updating a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resource : instance of {@link Resource}
	 * @return instance of updated {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource updateResourceApi(String apiId, Resource resource) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updateResourceApi(apiId, resource);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy in an api.
	 * Policy must be a Spike Arrest.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of updated {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy updatePolicyApi(String apiId, SpikeArrest p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy in an api.
	 * Policy must be a Quota.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of updated {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy updatePolicyApi(String apiId, Quota p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy in an api.
	 * Policy must be a IP Access Control.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of updated {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy updatePolicyApi(String apiId, IPAccessControl p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy in an api.
	 * Policy must be a Verify App key.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of updated {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy updatePolicyApi(String apiId, VerifyAppKey p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy in an api.
	 * Policy must be an OAuth.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link OAuth}
	 * @return instance of updated {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy updatePolicyApi(String apiId, OAuth p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy in an api.
	 * Policy must be a SAML.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param p : instance of {@link SAML}
	 * @return instance of updated {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy updatePolicyApi(String apiId, SAML p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * deleting an api data.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @throws CustomAuthenticationException
	 */
	public void deleteApi(String apiId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deleteApi(apiId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * deleting a resource from an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 *  
	 * @param apiId : String
	 * @param resourceId : String 
	 * @throws CustomAuthenticationException
	 */
	public void deleteResourceApi(String apiId, String resourceId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deleteResourceApi(apiId, resourceId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * deleting a policy from an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @throws CustomAuthenticationException
	 */
	public void deletePolicyApi(String apiId, String policyId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deletePolicyApi(apiId, policyId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * retrieving status of api data by its status name.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param statusName : String
	 * @return instance of {@link Status}
	 * @throws CustomAuthenticationException
	 */
	public Status getApiStatusByStatusName(String apiId, String statusName) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.getApiStatusByStatusName(apiId, statusName);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * retrieving list of status api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @return list of {@link Status} instance
	 * @throws CustomAuthenticationException
	 */
	public List<Status> getApiStatus(String apiId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.getApiStatus(apiId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding status to an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param s : instance of {@link Status}
	 * @return list of {@link Status} instance
	 * @throws CustomAuthenticationException
	 */
	public List<Status> addStatusApi(String apiId, Status s) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addStatusApi(apiId, s);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a status in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param s : instance of {@link Status}
	 * @return list of {@link Status} instance
	 * @throws CustomAuthenticationException
	 */
	public List<Status> updateStatusApi(String apiId, Status s) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updateStatusApi(apiId, s);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * deleting a status from an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param statusName : String
	 * @throws CustomAuthenticationException
	 */
	public void deleteStatusApi(String apiId, String statusName) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deleteStatusApi(apiId, statusName);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * retrieving policy data of an api by policy id.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param policyId : String
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy getPolicyApiByPolicyId(String apiId, String policyId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.getPolicyApiByPolicyId(apiId, policyId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * retrieving resource data of an api by resource id.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource getResourceApiByResourceId(String apiId, String resourceId) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.getResourceApiByResourceId(apiId, resourceId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * retrieving policy resource of an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Policy getPolicyResourceApiByResourceId(String apiId, String resourceId, String policyId) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.getPolicyResourceApiByResourceId(apiId, resourceId, policyId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a Spike Arrest policy to a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String 
	 * @param resourceId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource addPolicyResourceApi(String apiId, String resourceId, SpikeArrest p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy spike arrest is already in
			Resource r = pmanager.getResourceApiByResourceId(apiId, resourceId);
			List<Policy> listp = r.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof SpikeArrest){
						throw new IllegalArgumentException(
								"A policy spike arrest already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a Quota policy to a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * It throws an exception if a policy quota already exists for the resource.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of {@link Policy}
	 * @throws CustomAuthenticationException
	 */
	public Resource addPolicyResourceApi(String apiId, String resourceId, Quota p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy quota is already in
			Resource r = pmanager.getResourceApiByResourceId(apiId, resourceId);
			List<Policy> listp = r.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof Quota){
						throw new IllegalArgumentException(
								"A policy quota already exists, you cannot add a new one " +
								"before deleting it.");
					}
				}
			}
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding an IP Access Control policy to a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String 
	 * @param resourceId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource addPolicyResourceApi(String apiId, String resourceId, IPAccessControl p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy ip access control is already in
			Resource r = pmanager.getResourceApiByResourceId(apiId, resourceId);
			List<Policy> listp = r.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof IPAccessControl){
						throw new IllegalArgumentException(
								"A policy ip access control already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding an Verify app key policy to a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String 
	 * @param resourceId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource addPolicyResourceApi(String apiId, String resourceId, VerifyAppKey p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy ip access control is already in
			Resource r = pmanager.getResourceApiByResourceId(apiId, resourceId);
			List<Policy> listp = r.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof VerifyAppKey){
						throw new IllegalArgumentException(
								"A policy Verify App Key already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding an OAuth policy to a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String 
	 * @param resourceId : String
	 * @param p : instance of {@link OAuth}
	 * @return instance of {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource addPolicyResourceApi(String apiId, String resourceId, OAuth p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy oauth is already in
			Resource r = pmanager.getResourceApiByResourceId(apiId, resourceId);
			List<Policy> listp = r.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof OAuth){
						throw new IllegalArgumentException(
								"A policy OAuth already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * adding a SAML policy to a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String 
	 * @param resourceId : String
	 * @param p : instance of {@link SAML}
	 * @return instance of {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource addPolicyResourceApi(String apiId, String resourceId, SAML p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			
			//check if a policy SAML is already in
			Resource r = pmanager.getResourceApiByResourceId(apiId, resourceId);
			List<Policy> listp = r.getPolicy();
			if(listp!=null && listp.size()>0){
				for(int i=0;i<listp.size();i++){
					if(listp.get(i) instanceof SAML){
						throw new IllegalArgumentException(
								"A policy SAML already exists, " +
								"you cannot add a new one before deleting it.");
					}
				}
			}
			
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy of a resource in an api.
	 * Policy must be a Spike Arrest.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link SpikeArrest}
	 * @return instance of updated {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource updatePolicyResourceApi(String apiId, String resourceId, SpikeArrest p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy of a resource in an api.
	 * Policy must be a Quota.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link Quota}
	 * @return instance of updated {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource updatePolicyResourceApi(String apiId, String resourceId, Quota p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy of a resource in an api.
	 * Policy must be an IP Access Control.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link IPAccessControl}
	 * @return instance of updated {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource updatePolicyResourceApi(String apiId, String resourceId, IPAccessControl p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy of a resource in an api.
	 * Policy must be a Verify App Key.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link VerifyAppKey}
	 * @return instance of updated {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource updatePolicyResourceApi(String apiId, String resourceId, VerifyAppKey p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy of a resource in an api.
	 * Policy must be an OAuth.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link OAuth}
	 * @return instance of updated {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource updatePolicyResourceApi(String apiId, String resourceId, OAuth p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating a policy of a resource in an api.
	 * Policy must be a SAML.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param p : instance of {@link SAML}
	 * @return instance of updated {@link Resource}
	 * @throws CustomAuthenticationException
	 */
	public Resource updatePolicyResourceApi(String apiId, String resourceId, SAML p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * deleting a policy from a resource in an api.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param policyId : String
	 * @throws CustomAuthenticationException
	 */
	public void deletePolicyResourceApi(String apiId, String resourceId, String policyId) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deletePolicyResourceApi(apiId, resourceId, policyId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function retrieves owner of app from security context and then
	 * add a new app to db.
	 * 
	 * @param app : instance of {@link App}
	 * @return saved instance of {@link App}
	 */
	public App addApp(App app) {
		//owner id
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		app.setOwnerId(user);
		
		App nApp = pmanager.addApp(app);
		return nApp;
	}
	
	/**
	 * This function checks user permission before
	 * retrieving app data by its id.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param appId : String
	 * @return instance of {@link App}
	 * @throws CustomAuthenticationException
	 */
	public App getAppById(String appId) throws CustomAuthenticationException{
		App app = pmanager.getAppById(appId);
		if(security.canUserDoThisOperation(app.getOwnerId())){
			return pmanager.getAppById(appId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This functions retrieves app by owner id.
	 * 
	 * @return list of {@link App} instances
	 */
	public List<App> listAppByOwner() {
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		List<App> alist = pmanager.listApp(user);
		return alist;
	}
	
	/**
	 * This function checks user permission before
	 * updating an app.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param app : instance of {@link App}
	 * @return instance of updated {@link App}
	 * @throws CustomAuthenticationException
	 */
	public App updateApp(App app) throws CustomAuthenticationException{
		App savedApp = pmanager.getAppById(app.getId());
		if(security.canUserDoThisOperation(savedApp.getOwnerId())){
			return pmanager.updateApp(app);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * deleting an app.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param appId : String
	 * @throws CustomAuthenticationException
	 */
	public void deleteApp(String appId) throws CustomAuthenticationException{
		App app = pmanager.getAppById(appId);
		if(security.canUserDoThisOperation(app.getOwnerId())){
			pmanager.deleteApp(appId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * updating list of api from an app.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param app : instance of {@link App}
	 * @return instance of updated {@link App}
	 * @throws CustomAuthenticationException
	 */
	public App updateAppApiData(App app) throws CustomAuthenticationException{
		App sapp = pmanager.getAppById(app.getId());
		if(security.canUserDoThisOperation(sapp.getOwnerId())){
			return pmanager.updateAppApiData(app);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	/**
	 * This function checks user permission before
	 * deleting list of api from an app.
	 * It throws a security exception, if user has not the right
	 * permissions.
	 * 
	 * @param appId : String
	 * @param apiId : String
	 * @throws CustomAuthenticationException
	 */
	public void deleteAppApiData(String appId, String apiId) throws CustomAuthenticationException{
		App app = pmanager.getAppById(appId);
		if(security.canUserDoThisOperation(app.getOwnerId())){
			pmanager.deleteAppApiData(appId, apiId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
}
