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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.proxy.LastTime;
import eu.trentorise.smartcampus.api.manager.model.proxy.PolicyQuota;
import eu.trentorise.smartcampus.api.manager.repository.LastTimeRepository;
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
	 * Instance of {@link MongoOperations}.
	 */
	@Autowired
	private MongoOperations mongoOperations;
	/**
	 * Instance of {@link LastTimeRepository}
	 */
	@Autowired
	private LastTimeRepository spikeArRep;
	
	/*
	 * POLICY QUOTA MODEL
	 */
	
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
			//check that appId is null
			for(int i=0;i<pqlist.size();i++){
				if(pqlist.get(i).getAppId()==null){
					return pqlist.get(i);
				}
			}
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
	 * Find And Modify function.
	 * 
	 * @param apiId : String
	 * @param inTime : boolean
	 * @return new updated instance of {@link PolicyQuota}
	 */
	public PolicyQuota findAndModify(PolicyQuota p, boolean inTime){
		Query query = new Query();
		Criteria criteria = new Criteria().andOperator(
				Criteria.where("appId").is(p.getAppId()),
				Criteria.where("resourceId").is(p.getResourceId()),
				Criteria.where("apiId").is(p.getApiId())
				);
		query.addCriteria(criteria);
		
		Update update = new Update();
		update.set("time", new Date());
		if(inTime){
			update.inc("count",1);
		}else{
			update.set("count", 1);
		}
		
		//FindAndModifyOptions().returnNew(true) = newly updated document
		PolicyQuota pq = mongoOperations.findAndModify(query, update, 
				new FindAndModifyOptions().returnNew(true), PolicyQuota.class);
		return pq;
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
	public void deletePolicyQuota(String id){
		pqrep.delete(id);
	}
	
	/*
	 * POLICY SPIKE ARREST 
	 */
	
	/**
	 * Retrieves policy data spike arrest searching by all parameters:
	 * api, resource and app id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param appId : String
	 * @return instance of {@link LastTime}
	 */
	public LastTime retrievePolicySpikeArrestByApiAndRAndAppId(String apiId, String resourceId, String appId){
		List<LastTime> slist =  spikeArRep.findByApiIdAndResourceIdAndAppId(apiId, resourceId, appId);
		if(slist!=null && slist.size()>0){
			return slist.get(0);
		}

		return null;
	}
	
	/**
	 * Retrieves policy data spike arrest searching by all parameters:
	 * api and resource id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link LastTime}
	 */
	public LastTime retrievePolicySpikeArrestByApiAndResouceId(String apiId, String resourceId){
		List<LastTime> slist =  spikeArRep.findByApiIdAndResourceId(apiId, resourceId);
		if(slist!=null && slist.size()>0){
			//check that appId is null
			for(int i=0;i<slist.size();i++){
				if(slist.get(i).getAppId()==null){
					return slist.get(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Adds new data for spike arrest policy.
	 * 
	 * @param lt : instance of {@link LastTime}
	 * @return new instance of {@link LastTime}
	 */
	public LastTime addPolicySpikeArrest(LastTime lt){
		return spikeArRep.save(lt);
	}
	
	/**
	 * Find And Modify function.
	 * 
	 * @param l : instance of {@link LastTime}
	 * @return new updated instance of {@link LastTime}
	 */
	public LastTime findAndModify(LastTime l){
		Query query = new Query();
		Criteria criteria = new Criteria().andOperator(
				Criteria.where("appId").is(l.getAppId()),
				Criteria.where("resourceId").is(l.getResourceId()),
				Criteria.where("apiId").is(l.getApiId())
				);
		query.addCriteria(criteria);
		
		Update update = new Update();
		update.set("time", new Date());
		
		//FindAndModifyOptions().returnNew(true) = newly updated document
		LastTime lt = mongoOperations.findAndModify(query, update, 
				new FindAndModifyOptions().returnNew(true), LastTime.class);
		return lt;
	}
	
	/**
	 * Deletes an entry of spike arrest policy.
	 * 
	 * @param lastTimeId : String
	 */
	public void addPolicySpikeArrest(String lastTimeId){
		spikeArRep.delete(lastTimeId);
	}
	
}
