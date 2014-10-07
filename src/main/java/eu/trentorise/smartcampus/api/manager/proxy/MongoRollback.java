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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.smartcampus.api.manager.model.proxy.LastTime;
import eu.trentorise.smartcampus.api.manager.model.proxy.PolicyQuota;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManagerProxy;

/**
 * Class that implements a transaction rollback for MongoDb
 * 
 * @author Giulia Canobbio
 *
 */
public class MongoRollback {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(MongoRollback.class);
	/**
	 * Instance of {@link PersistenceManagerProxy}.
	 */
	private PersistenceManagerProxy pmanager;
	
	/**
	 * 
	 * @param pmanager : instance of {@link PersistenceManagerProxy}
	 */
	public void setPmanager(PersistenceManagerProxy pmanager) {
		this.pmanager = pmanager;
	}
	
	//TODO
	public void failurePolicy(String apiId, String resourceId, String appId, String policyType){
		logger.info("Failure..");
		if(policyType.equalsIgnoreCase("Spike Arrest")){
			logger.info("F. Spike Arrest ..");
			//find a LastTime entry with this parameter and state pending
			LastTime lt = pmanager.retrievePolicySpikeArrestByApiAndRAndAppId(apiId, resourceId, appId);
			if(lt!=null){
				//retrieve date
				if(lt.getState().equalsIgnoreCase("pending")){
					lt.setTime(lt.getPrevTime());
					//re-insert state initial (done)
					lt.setState("done");
					pmanager.findAndModify(lt);
				}
			}
			
		}
		if(policyType.equalsIgnoreCase("Quota")){
			logger.info("F. Quota ..");
			//find a PolicyQuota entry with this parameter and state pending
			PolicyQuota pq = pmanager.retrievePolicyQuotaByParamIds(apiId, resourceId, appId);
			if(pq!=null){
				if (pq.getState().equalsIgnoreCase("pending")) {
					// count--
					int count = pq.getCount();
					if (pq.getCount() > 1) {
						pq.setCount(count--);
					}
					// retrieve date to initial
					pq.setTime(pq.getPrevTime());
					// re-insert state initial (done)
					pq.setState("done");
					pmanager.findAndModify(pq);
				}
			}
		}
	}
	
	public void successfulPolicySP(String apiId, String resourceId, String appId) {
		LastTime lt = pmanager.retrievePolicySpikeArrestByApiAndRAndAppId(
				apiId, resourceId, appId);
		if (lt != null) {
			// re-insert state initial (done)
			lt.setState("done");
			pmanager.findAndModify(lt);
		}

	}
	
	public void successfulPolicyQ(String apiId, String resourceId, String appId) {

		PolicyQuota pq = pmanager.retrievePolicyQuotaByParamIds(apiId,
				resourceId, appId);
		if (pq != null) {
			// re-insert state initial (done)
			pq.setState("done");
			pmanager.findAndModify(pq);
		}

	}

}
