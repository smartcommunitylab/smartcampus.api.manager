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




//import org.springframework.data.mongodb.core.mapping.*;

/**
 * Policy IP Access Control:
 * rule: DENY or ALLOW,
 * white list: list of allowed ip,
 * black list: list of not allowed ip.
 * 
 * @author Giulia Canobbio
 *
 */
//@Document
public class IPAccessControl extends Policy{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	//@Field
	private String rule;
	//@Field("wlist")
	private List<SourceAddress> whiteList;
	//@Field("blist")
	private List<SourceAddress> blackList;
	
	/**
	 * 
	 * @return rule : String
	 */
	public String getRule() {
		return rule;
	}
	/**
	 * 
	 * @param rule : String
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}
	/**
	 * 
	 * @return allowed ip address : list of {@link SourceAddress} instances
	 */
	public List<SourceAddress> getWhiteList() {
		return whiteList;
	}
	/**
	 * 
	 * @param whiteList : list of {@link SourceAddress} instances
	 */
	public void setWhiteList(List<SourceAddress> whiteList) {
		this.whiteList = whiteList;
	}
	/**
	 * 
	 * @return not allowed ip address : list of {@link SourceAddress} instances
	 */
	public List<SourceAddress> getBlackList() {
		return blackList;
	}
	/**
	 * 
	 * @param blackList : list of {@link SourceAddress} instances
	 */
	public void setBlackList(List<SourceAddress> blackList) {
		this.blackList = blackList;
	}
	
	

}
