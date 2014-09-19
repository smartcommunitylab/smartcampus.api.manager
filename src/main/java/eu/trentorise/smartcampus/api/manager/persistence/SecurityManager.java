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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;
import eu.trentorise.smartcampus.api.manager.model.Status;
import eu.trentorise.smartcampus.api.security.CustomAuthenticationException;

public class SecurityManager {
	
	@Autowired
	private PersistenceManager pmanager;
	@Autowired
	private PermissionManager security;

	public Api getApiById(String apiId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return api;
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public Api getApiByName(String apiName) throws CustomAuthenticationException{
		Api api = pmanager.getApiByName(apiName);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return api;
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public List<Api> getApiByOwnerId() throws CustomAuthenticationException{
		String ownerId = security.getUsername();
		return pmanager.getApiByOwnerId(ownerId);
	}
	
	public Resource addResourceApi(String apiId, Resource resource) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addResourceApi(apiId, resource);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}

	public Policy addPolicyApi(String apiId, SpikeArrest p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public Policy addPolicyApi(String apiId, Quota p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addPolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
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
	
	public Policy updatePolicyApi(String apiId, SpikeArrest p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public Policy updatePolicyApi(String apiId, Quota p) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updatePolicyApi(apiId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public void deleteApi(String apiId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deleteApi(apiId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public void deleteResourceApi(String apiId, String resourceId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deleteResourceApi(apiId, resourceId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public void deletePolicyApi(String apiId, String policyId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deletePolicyApi(apiId, policyId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
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
	
	public List<Status> getApiStatus(String apiId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.getApiStatus(apiId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public List<Status> addStatusApi(String apiId, Status s) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addStatusApi(apiId, s);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public List<Status> updateStatusApi(String apiId, Status s) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.updateStatusApi(apiId, s);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public void deleteStatusApi(String apiId, String statusName) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			pmanager.deleteStatusApi(apiId, statusName);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public Policy getPolicyApiByPolicyId(String apiId, String policyId) throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.getPolicyApiByPolicyId(apiId, policyId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
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
	
	public Resource addPolicyResourceApi(String apiId, String resourceId, SpikeArrest p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public Resource addPolicyResourceApi(String apiId, String resourceId, Quota p) 
			throws CustomAuthenticationException{
		Api api = pmanager.getApiById(apiId);
		if(security.canUserDoThisOperation(api.getOwnerId())){
			return pmanager.addPolicyResourceApi(apiId, resourceId, p);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
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
	
	public App getAppById(String appId) throws CustomAuthenticationException{
		App app = pmanager.getAppById(appId);
		if(security.canUserDoThisOperation(app.getOwnerId())){
			return pmanager.getAppById(appId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public App updateApp(App app) throws CustomAuthenticationException{
		App savedApp = pmanager.getAppById(app.getId());
		if(security.canUserDoThisOperation(savedApp.getOwnerId())){
			return pmanager.updateApp(app);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public void deleteApp(String appId) throws CustomAuthenticationException{
		App app = pmanager.getAppById(appId);
		if(security.canUserDoThisOperation(app.getOwnerId())){
			pmanager.deleteApp(appId);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
	public App updateAppApiData(App app) throws CustomAuthenticationException{
		App sapp = pmanager.getAppById(app.getId());
		if(security.canUserDoThisOperation(sapp.getOwnerId())){
			return pmanager.updateAppApiData(app);
		}
		else {
			throw new CustomAuthenticationException("You are not allowed");
		}
	}
	
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
