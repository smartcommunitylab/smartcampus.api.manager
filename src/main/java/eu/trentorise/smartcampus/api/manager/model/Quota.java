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
 * Policy model of Quota
 * name : String, name of policy instance. The name must be unique in the organization.
 * interval : (required) Integer, specifies interval of time (hours, minutes or days defined in time unit).
 * timeUnit : (required) String, valid values: second, minute, hour, day, or month.
 * allow count : (required) Integer, specifies a message count.
 * identifier : (optional) String, variable used for uniquely identifying the client app.
 * 
 * @author Giulia Canobbio
 *
 */
@Document
public class Quota extends Policy{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	@Field("quota-name")
	@Indexed(unique=true)
	private String qName;
	@Field("quota-interval")
	private Integer interval;
	@Field("quota-TimeUnit")
	private String timeUnit;
	@Field("quota-Allow count")
	private Integer allowCount;
	@Field("quota-Identifier")
	private String identifier;
	
	/**
	 * 
	 * @return quota name : String
	 */
	public String getqName() {
		return qName;
	}
	/**
	 * 
	 * @param qName : String, quota name
	 */
	public void setqName(String qName) {
		this.qName = qName;
	}
	/**
	 * 
	 * @return quota Interval : Integer
	 */
	public Integer getInterval() {
		return interval;
	}
	/**
	 * 
	 * @param interval : Integer, quota interval
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}
	/**
	 * 
	 * @return quota TimeUnit : String
	 */
	public String getTimeUnit() {
		return timeUnit;
	}
	/**
	 * 
	 * @param timeUnit : String, quota TimeUnit
	 */
	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}
	/**
	 * 
	 * @return quota Allow count : Integer
	 */
	public Integer getAllowCount() {
		return allowCount;
	}
	/**
	 * 
	 * @param allowCount : Integer, quota Allow count
	 */
	public void setAllowCount(Integer allowCount) {
		this.allowCount = allowCount;
	}
	/**
	 * 
	 * @return quota Identifier : String
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * 
	 * @param identifier : String, quota Identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
