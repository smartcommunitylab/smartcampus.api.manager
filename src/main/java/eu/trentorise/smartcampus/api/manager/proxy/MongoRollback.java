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

/**
 * Class that implements a transaction rollback for MongoDb
 * 
 * @author Giulia Canobbio
 *
 */
public class MongoRollback {
	//TODO
	public void failurePolicy(String apiId, String resourceId, String appId, String policyType){
		if(policyType.equalsIgnoreCase("Spike Arrest")){
			//find a LastTime entry with this parameter and state pending
			//retrieve date
			//re-insert state initial (done)
		}
		if(policyType.equalsIgnoreCase("Quota")){
			//find a PolicyQuota entry with this parameter and state pending
			//count--
			//retrieve date to initial 
			//re-insert state initial (done)
		}
	}
	
	public void successfulPolicy(String apiId, String resource, String appId, String policyType){
		if(policyType.equalsIgnoreCase("Spike Arrest")){
			//find a LastTime entry with this parameter and state pending
			//set state to done
		}
		if(policyType.equalsIgnoreCase("Quota")){
			//find a PolicyQuota entry with this parameter and state pending
			//set state to done
		}
	}

}
