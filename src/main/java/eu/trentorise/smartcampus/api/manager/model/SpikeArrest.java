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
package eu.trentorise.smartcampus.api.manager.model;




//import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Policy model of Spike Arrest
 * name : String, name of policy instance. The name must be unique in the organization.
 * rate : String, specifies the rate at which to limit the traffic spike (or burst). Valid value: integer per min or sec.
 * identifier : (optional) String, variable used for uniquely identifying the client.
 * 
 * @author Giulia Canobbio
 *
 */
//@Document
public class SpikeArrest extends Policy{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	//@Field
	public String rate;
	//@Field
	public boolean global;

	/**
	 * 
	 * @return rate : String
	 */
	public String getRate() {
		return rate;
	}
	/**
	 * 
	 * @param rate : String
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}
	/**
	 * 
	 * @return global : boolean
	 */
	public boolean isGlobal() {
		return global;
	}
	/**
	 * 
	 * @param global : boolean
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	
}
