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
package eu.trentorise.smartcampus.api.manager.model.util;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
 * Response of oauth endpoint
 *
 * @author Giulia Canobbio
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateToken {
	
	private boolean active;
	private Date exp;
	
	/**
	 * 
	 * @return active : boolean
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * 
	 * @param active : boolean
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * 
	 * @return expire : Date
	 */
	public Date getExp() {
		return exp;
	}
	/**
	 * 
	 * @param exp : Date
	 */
	public void setExp(Date exp) {
		this.exp = exp;
	}
	
}
