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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.api.manager.model.Policy;
import eu.trentorise.smartcampus.api.manager.model.Quota;
import eu.trentorise.smartcampus.api.manager.model.SpikeArrest;

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
	/**
	 * Instance of {@link QuotaApply}.
	 */
	@Autowired
	private QuotaApply qapply;
	/**
	 * Instance of {@link SpikeArrestApply}.
	 */
	@Autowired
	private SpikeArrestApply spapply;

	@Override
	public void apply(Policy p) {
		// TODO Auto-generated method stub
		if(p instanceof Quota){
			logger.info("Quota policy..");
			qapply.apply(p);
		}else if(p instanceof SpikeArrest){
			logger.info("Spike Arrest policy..");
			spapply.apply(p);
		}else{
			logger.info("Ops.. Not yet implemented...We are sorry!");
		}
	}

}
