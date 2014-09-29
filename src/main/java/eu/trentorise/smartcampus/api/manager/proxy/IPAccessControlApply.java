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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.SourceAddress;

public class IPAccessControlApply implements PolicyDatastoreApply{
	/**
	 * Instance of {@link Logger)
	 */
	private static final Logger logger = LoggerFactory.getLogger(IPAccessControlApply.class);
	
	//Global variable
	private IPAccessControl pac;
	private String apiId;
	private String resourceId;
	//private String appId;
	private String appIp;
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param apiId : String
	 * @param resourceId : String
	 * @param pac : instance of {@link IPAccessControl}
	 * @param appIp : String
	 */
	public IPAccessControlApply(String apiId, String resourceId/*, String appId*/, IPAccessControl pac, 
			String appIp){
		this.pac = pac;
		this.apiId = apiId;
		this.resourceId = resourceId;
		//this.appId = appId;
		this.appIp = appIp;// read when the request is done
	}

	@Override
	public void apply() {
		//start apply ip access control
		decision();
	}
	
	/**
	 * Function that decide if an access to resource or api with a specific ip can be granted or not 
	 * by applying ip access control policy.
	 */
	private void decision(){
		boolean decision;
		
		// check that resource id cannot be null
		if (apiId == null && resourceId == null) {
			throw new IllegalArgumentException("Api or Resource id cannot be null.");
		}
		else{
			decision=IpAccessControlDecision(pac.getBlackList(), pac.getWhiteList(), appIp, pac.getRule());
		}
		
		if(decision)
			logger.info("Ip Access Control policy --> GRANT ");
		else
			logger.info("Ip Access Control  policy --> DENY ");
	}
	
	/**
	 * Function that check if the app ip is in white list or in blacklist.
	 * Then it checks the main rule (ALLOW or DENY) and decide to grant or deny
	 * access.
	 * 
	 * @param BL : list of {@link SourceAddress}, list of allowed ip and mask
	 * @param WL : list of {@link SourceAddress}, list of denied ip and mask
	 * @param appIp : String, ip of app that make the request
	 * @param noRuleMatchAction : String
	 * @return boolean value, true if it is granted otherwise false
	 */
	public  boolean IpAccessControlDecision(List<SourceAddress> BL, List<SourceAddress> WL, 
			String appIp, String noRuleMatchAction) {	
		
		String appIp_24=appIpMask(appIp)[0]; 
		String appIp_16=appIpMask(appIp)[1]; 
		
		boolean condBL=BL.contains(appIp) || BL.contains(appIp_24) || BL.contains(appIp_16);
		boolean condWL=WL.contains(appIp) || WL.contains(appIp_24) || WL.contains(appIp_16);
		
		boolean decision=false;
		
		if(noRuleMatchAction.equalsIgnoreCase("ALLOW")){	
			if( condBL==true && condWL==false ){
				decision= false;
			}else
				decision= true;
		}
		if(noRuleMatchAction.equalsIgnoreCase("DENY")){
			if( condBL==false && condWL==true ){
				decision= true;
			}else
				decision= false;
		}
		return decision;
	}

	/**
	 * Return string value of a ip in bit.
	 * 
	 * @param appIp : String
	 * @return string value of ip
	 */
	private String[] appIpMask(String appIp){
	
		String contrary=new StringBuilder(appIp).reverse().toString();
		int endIndex = appIp.length()-contrary.indexOf('.')-1;	
		String appIp_24 =appIp.substring(0, endIndex );

		String contrary2=new StringBuilder(appIp_24).reverse().toString();
		int endIndex2 = appIp_24.length()-contrary2.indexOf('.')-1;	
		String appIp_16 =appIp.substring(0, endIndex2 );
		String[] ip=new String[]{appIp_24,appIp_16};
		return ip;
	}

	

}
