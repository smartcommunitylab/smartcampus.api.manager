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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class that apply the correct logic to policy.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
public class PolicyDatastoreBatch implements PolicyDatastoreApply{
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PolicyDatastoreBatch.class);
	
	private List<PolicyDatastoreApply> policies = new ArrayList<PolicyDatastoreApply>();

	@Override
	public void apply() {
		logger.info("Apply - PolicyDatastoreBatch");
		for(int i=0;i<policies.size();i++){
			policies.get(i).apply();
		}
		
	}
	
	/**
	 * Add element to list.
	 * 
	 * @param p : instance of {@link PolicyDatastoreApply}
	 */
	public void add(PolicyDatastoreApply p){
		policies.add(p);
	}
	
	/**
	 * Remove element from list.
	 * 
	 * @param p : instance of {@link PolicyDatastoreApply}
	 */
	public void remove(PolicyDatastoreApply p){
		policies.remove(p);
	}
	
	/**
	 * Clean list.
	 */
	public void clean(){
		policies.clear();
	}

}
