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

/**
 * For testing endpoint of oauth
 */
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidateToken {
	
	private boolean active;
	private Date exp;
	private String scope;
	private String client_id;
	private String user_id;
	
	/**
	 * Constructor.
	 * Random date.
	 * Active value.
	 */
	public ValidateToken(){
		active = false;
		
		Date today = new Date();
		//Random date
		long beginTime = Timestamp.valueOf("2014-10-10 15:10:00").getTime();
		long endTime = Timestamp.valueOf("2014-12-31 15:10:00").getTime();
		long diff = endTime - beginTime +1;
		
		long random = beginTime + (long)(Math.random() * diff);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		exp = new Date(random);
		
		System.out.println(dateFormat.format(exp));
		
		//active value
		if(today.before(exp)){
			active = true;
		}
	}
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
	/**
	 * 
	 * @return scope : String
	 */
	public String getScope() {
		return scope;
	}
	/**
	 * 
	 * @param scope : String
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	/**
	 * 
	 * @return client id : String
	 */
	public String getClient_id() {
		return client_id;
	}
	/**
	 * 
	 * @param client_id : String
	 */
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	/**
	 * 
	 * @return user id : String
	 */
	public String getUser_id() {
		return user_id;
	}
	/**
	 * 
	 * @param user_id : String
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	

}
