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

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Policy model of Spike Arrest
 * name : name of policy instance. The name must be unique in the organization.
 * rate : specifies the rate at which to limit the traffic spike (or burst). Valid value: integer per min or sec.
 * identifier : (optional) variable used for uniquely identifying the client.
 * 
 * @author Giulia Canobbio
 *
 */
@Document
public class SpikeArrest extends Policy{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	@Field("spike-arrest-name")
	@Indexed
	public String sName;
	@Field("spike-arrest-rate")
	public String rate;
	@Field("spike-arrest-identifier")
	public String identifier;
	
	/**
	 * 
	 * @return sName : String, spike arrest name
	 */
	public String getsName() {
		return sName;
	}
	/**
	 * 
	 * @param sName : String, spike arrest name
	 */
	public void setsName(String sName) {
		this.sName = sName;
	}
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
	 * @return identifier : String
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * 
	 * @param identifier : String
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	
}
