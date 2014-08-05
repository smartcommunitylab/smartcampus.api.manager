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
package eu.trentorise.smartcampus.api.manager.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Class that retrieves policies of api and its resources.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
public class PolicyDecisionPort {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PolicyDecisionPort.class);
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	@Autowired
	private PersistenceManager manager;
	/**
	 * Instance of {@link PolicyDatastoreBatch}.
	 */
	@Autowired
	private PolicyDatastoreBatch batch;
	/*
	 * Global variables
	 */
	private String apiId;
	private List<String> resourcesId;
	private List<Policy> pToApply;
	
	/**
	 * Init array global variables.
	 */
	private void init(){
		if(resourcesId==null){
			resourcesId = new ArrayList<String>();
		}
		if(pToApply==null){
			pToApply = new ArrayList<Policy>();
		}
	}
	
	/**
	 * Query hashmap and retrieve data, such as
	 * ApiID, Resources IDs and Request Headers.
	 * 
	 * @param map : HashMap
	 */
	private void getData(HashMap<String, String> map){
		apiId = map.get("ApiID");
		//remove
		//map.remove("ApiID");
		
		//find how many resources are saved in map
		List<String> resourceKey = new ArrayList<String>();
		
		Set<String> keylist = map.keySet();
		Iterator<String> iter = keylist.iterator();
		while(iter.hasNext()){
			String element = iter.next();
			if(element.contains("Resource")){
				resourceKey.add(element);
			}
		}
		
		//retrieve values from map
		for(int i=0;i<resourceKey.size();i++){
			//print key
			logger.info("GetData(): Resource key: {}", resourceKey.get(i));
			
			String rvalue = map.get(resourceKey.get(i));
			resourcesId.add(rvalue);
			//remove
			//map.remove(resourceKey.get(i));
		}
		
		//if remove
		//now in map exists only headers
		
	}
	
	/**
	 * Retrieves api policies and resource policies.
	 * 
	 */
	private void policiesList(){
		logger.info("policiesList() - ApiId: {}", apiId);
		//api policies
		Api api =  manager.getApiById(apiId);
		List<Policy> policies = api.getPolicy();
		if(policies!=null && policies.size()>0){
			pToApply.addAll(policies);
		}
		
		//resource policies
		if (resourcesId != null && resourcesId.size() > 0) {
			for (int i = 0; i < resourcesId.size(); i++) {
				logger.info("policiesList() - ResourceId: {}",
						resourcesId.get(i));
				Resource r = manager.getResourceApiByResourceId(apiId,
						resourcesId.get(i));
				List<Policy> rplist=r.getPolicy();
				if(rplist!=null && rplist.size()>0){
					pToApply.addAll(rplist);
				}
			}
		}
	}
	
	/**
	 * Apply policy logic to the request.
	 * 
	 * @param map : HashMap
	 */
	public void applyPoliciesBatch(HashMap<String, String> map){
		logger.info("applyPoliciesBatch() - Apply policies....");
		init();
		getData(map);
		policiesList();
		
		if (pToApply != null && pToApply.size() > 0) {
			for (int i = 0; i < pToApply.size(); i++) {
				logger.info("applyPoliciesBatch - Apply for each");
				batch.apply(pToApply.get(i));
			}
		} else {
			logger.info("applyPoliciesBatch - No policies to apply");
		}
	}
}
