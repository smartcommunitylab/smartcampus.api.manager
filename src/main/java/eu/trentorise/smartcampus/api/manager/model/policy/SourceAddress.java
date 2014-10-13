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
package eu.trentorise.smartcampus.api.manager.model.policy;

import java.io.Serializable;

/**
 * Source address:
 * mask
 * ip
 * 
 * @author Giulia Canobbio
 *
 */
public class SourceAddress implements Serializable{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private int mask;
	private String ip;
	
	/**
	 * 
	 * @return mask : int
	 */
	public int getMask() {
		return mask;
	}
	/**
	 * 
	 * @param mask : int
	 */
	public void setMask(int mask) {
		this.mask = mask;
	}
	/**
	 * 
	 * @return ip : String
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * 
	 * @param ip : String
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
		
}
