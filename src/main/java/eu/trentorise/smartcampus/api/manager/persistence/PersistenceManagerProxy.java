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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.proxy.GlobalCounter;
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
	 * Retrieves Policy Quota data searching by api and resource id.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return instance of {@link PolicyQuota}
	 */
	/*public PolicyQuota retrievePolicyQuotaByParams(String apiId, String resourceId){
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
	}*/
	
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
	 * If collection is not found, then it is created.
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
		update.set("apiId", p.getApiId());
		update.set("resourceId", p.getResourceId());
		update.set("appId", p.getAppId());
		update.set("time", new Date());
		if(inTime){
			update.inc("count",1);
		}else{
			update.set("count", 1);
		}
		
		//for callback
		update.set("prevTime", p.getPrevTime());
		update.set("state",p.getState());
		
		//FindAndModifyOptions().returnNew(true) = newly updated document
		PolicyQuota pq = mongoOperations.findAndModify(query, update, 
				new FindAndModifyOptions().upsert(true).returnNew(true), PolicyQuota.class);
		return pq;
	}
	
	/**
	 * Find And Modify function.
	 * If collection is not found, then it is created.
	 * For rollback.
	 * 
	 * @param p : instance of {@link PolicyQuota}
	 * @return instance of {@link PolicyQuota}
	 */
	public PolicyQuota findAndModify(PolicyQuota p){
		Query query = new Query();
		Criteria criteria = new Criteria().andOperator(
				Criteria.where("appId").is(p.getAppId()),
				Criteria.where("resourceId").is(p.getResourceId()),
				Criteria.where("apiId").is(p.getApiId())
				);
		query.addCriteria(criteria);
		
		Update update = new Update();
		update.set("apiId", p.getApiId());
		update.set("resourceId", p.getResourceId());
		update.set("appId", p.getAppId());
		update.set("time", new Date());
		update.set("count",p.getCount());
		
		//for callback
		update.set("prevTime", p.getPrevTime());
		update.set("state",p.getState());
		
		//FindAndModifyOptions().returnNew(true) = newly updated document
		PolicyQuota pq = mongoOperations.findAndModify(query, update, 
				new FindAndModifyOptions().upsert(true).returnNew(true), PolicyQuota.class);
		return pq;
	}
	
	/**
	 * If global is set to true in policy quota, then count must be
	 * sum of count of anonymous and verified access to that
	 * resource or api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return total sum of count : int
	 */
	public int sumGlobalQuota(String apiId, String resourceId){
		int total = 0;
		
		Criteria criteria = new Criteria().andOperator(
				Criteria.where("apiId").is(apiId),
				Criteria.where("resourceId").is(resourceId)
				);
		Aggregation agg = newAggregation(
				match(criteria),
				group("apiId","resourceId").sum("count").as("total"));
		
		AggregationResults<GlobalCounter> p =  
				mongoOperations.aggregate(agg, "policyQuota", GlobalCounter.class);
		
		List<GlobalCounter> r = p.getMappedResults();
		if(r!=null && r.size()>0){
			total = r.get(0).getTotal();
		}
		
		return total;
		
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
	 * Find And Modify function.
	 * If collection is not found, then it is created.
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
		update.set("apiId", l.getApiId());
		update.set("resourceId", l.getResourceId());
		update.set("appId", l.getAppId());
		update.set("time", new Date());
		
		//for callback
		update.set("prevTime", l.getPrevTime());
		update.set("state", l.getState());
		
		//FindAndModifyOptions().returnNew(true) = newly updated document
		LastTime lt = mongoOperations.findAndModify(query, update, 
				new FindAndModifyOptions().upsert(true).returnNew(true), LastTime.class);
		return lt;
	}
	
	/**
	 * If global is set to true in policy spike arrest, then date must be
	 * the max date of anonymous and verified access to that
	 * resource or api.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @return max : Date
	 */
	public Date dateGlobalSpikeArrest(String apiId, String resourceId){
		Date max = null;
		//List<LastTime> slist =  spikeArRep.findByApiIdAndResourceId(apiId, resourceId);
		
		Query query = new Query();
		Criteria criteria = new Criteria().andOperator(
				Criteria.where("apiId").is(apiId),
				Criteria.where("resourceId").is(resourceId)
				);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "time"));
		
		List<LastTime> slist = mongoOperations.find(query, LastTime.class);
		
		if(slist!=null && slist.size()>0){
			max = slist.get(0).getTime();
			/*for(int i=0;i<slist.size();i++){
				if(slist.get(i).getTime().after(max)){//max.before(slist.get(i).getTime())
					max = slist.get(i).getTime();
				}
			}*/
		}
		return max;
	}
	
}
