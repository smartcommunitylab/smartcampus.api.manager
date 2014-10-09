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

import java.util.List;

//import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Policy model of Quota
 * name : String, name of policy instance. The name must be unique in the organization.
 * interval : (required) Integer, specifies interval of time (hours, minutes or days defined in time unit).
 * timeUnit : (required) String, valid values: second, minute, hour, day, or month.
 * allow count : (required) Integer, specifies a message count.
 * identifier : (optional) String, variable used for uniquely identifying the client app.
 * quota status : List of {@link QuotaStatus} instances, that has name and quota value.
 * 
 * @author Giulia Canobbio
 *
 */
//@Document
public class Quota extends Policy{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	//@Field
	private Integer interval;
	//@Field
	private String timeUnit;
	//@Field
	private Integer allowCount;
	//@Field
	private boolean global;
	//@Field
	private List<QuotaStatus> qstatus;
	
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
	/**
	 * 
	 * @return list of {@link QuotaStatus} instances
	 */
	public List<QuotaStatus> getQstatus() {
		return qstatus;
	}
	/**
	 * 
	 * @param qstatus : list of {@link QuotaStatus} instances
	 */
	public void setQstatus(List<QuotaStatus> qstatus) {
		this.qstatus = qstatus;
	}
}
