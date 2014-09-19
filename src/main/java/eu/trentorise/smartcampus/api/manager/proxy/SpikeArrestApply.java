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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.api.manager.model.proxy.LastTime;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManagerProxy;

/**
 * Class that apply Spike Arrest logic.
 * 
 * @author Giulia Canobbio.
 *
 */
@Component
public class SpikeArrestApply implements PolicyDatastoreApply{
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(SpikeArrestApply.class);
	/**
	 * Instance of {@link PersistenceManagerProxy}.
	 */
	private PersistenceManagerProxy pmanager;
	/**
	 * Instance of {@link PersistenceManager}.
	 */
	private PersistenceManager manager;
	
	/**
	 * 
	 * @param pmanager : instance of {@link PersistenceManagerProxy}
	 */
	public void setPManager(PersistenceManagerProxy pmanager){
		this.pmanager = pmanager;
	}
	
	/**
	 * 
	 * @param manager : instance of {@link PersistenceManager}
	 */
	public void setManager(PersistenceManager manager){
		this.manager = manager;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		logger.info("Applying spike arrest policy..");
	}
	
	public void decision(){
		String appId= "";//lastTime.getAppId();
		String resourceId="";//lastTime.getResourceId();
		String apiId="";//lastTime.getApiId();
		
		//valori che verranno letti da env attribute (per ora settiamolo)
		Date currentTime= new Date();
	
		boolean decision;
		 String message;
		//check that resource id cannot be null
		if(apiId==null && resourceId==null) {
			throw new IllegalArgumentException("Api or Resource id cannot be null.");
		   }else{
			   //retrieve spike arrest apply data
			   LastTime ltime = pmanager.retrievePolicySpikeArrestByApiAndResouceId(apiId, resourceId);
			   
			   if(ltime!=null){
				   logger.info("Last time {}", ltime);
				   if(appId==null){
					   
						decision=SpikeArrestDecision(Ratexml,apiId,resourceId,currentTime);
						}else{
							decision=SpikeArrestDecision(Ratexml,apiId,appId,resourceId,currentTime);}
			   }else{
				   decision=true;
			   }
			  }		
		
		if(decision)
			logger.info("Spike Arrest policy --> GRANT ");
		else
			logger.info("Spike Arrest policy --> DENY ");
	}

}
