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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.proxy.PolicyQuota;
import eu.trentorise.smartcampus.api.manager.repository.PolicyQuotaRepository;

@Component
@Transactional
public class PersistenceManagerProxy {
	/**
	 * Instance of {@link PolicyQuotaRepository}.
	 */
	@Autowired
	private PolicyQuotaRepository pqrep;
	
	/**
	 * Retrieves Policy Quota data by id.
	 * 
	 * @param id : String, mongo id
	 * @return instance of {@link PolicyQuota}
	 */
	public PolicyQuota retrievePolicyQuotaById(String id){
		List<PolicyQuota> pqlist = pqrep.findById(id);
		if(pqlist!=null && pqlist.size()>0){
			return pqlist.get(0);
		}
		return null;
	}
	
	/**
	 * Retrieves Policy Quota data searching by api and resource id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link PolicyQuota}
	 */
	public PolicyQuota retrievePolicyQuotaByParams(String apiId, String resourceId){
		List<PolicyQuota> pqlist =  pqrep.findByApiIdAndResourceId(apiId, resourceId);
		if(pqlist!=null && pqlist.size()>0){
			return pqlist.get(0);
		}
		return null;
	}
	
	/**
	 * Retrieves Policy Quota data searching by api, resource and app id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @return instance of {@link PolicyQuota}
	 */
	public PolicyQuota retrievePolicyQuotaByParamIds(String apiId, String resourceId, String appId){
		List<PolicyQuota> pqlist =  pqrep.findByApiIdAndResourceIdAndAppId(apiId, resourceId, appId);
		if(pqlist!=null && pqlist.size()>0){
			return pqlist.get(0);
		}
		return null;
	}
	
	/**
	 * Save a Policy Quota element in db.
	 * 
	 * @param pq : instance of {@link PolicyQuota}
	 * @return saved instance of {@link PolicyQuota}
	 */
	public PolicyQuota addPolicyQuota(PolicyQuota pq){
		return pqrep.save(pq);
	}
	
	/**
	 * Update a Policy Quota element saved in db.
	 * 
	 * @param pq : instance of {@link PolicyQuota}
	 * @return updated instance of {@link PolicyQuota}
	 */
	public PolicyQuota updatePolicyQuota(PolicyQuota pq){
		return addPolicyQuota(pq);
	}
	
	/**
	 * Delete a Policy Quota element from db.
	 * 
	 * @param id : String
	 */
	public void removePolicyQuota(String id){
		pqrep.delete(id);
	}
}
