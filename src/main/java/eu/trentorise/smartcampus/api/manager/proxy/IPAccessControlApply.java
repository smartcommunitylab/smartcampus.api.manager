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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.smartcampus.api.manager.model.IPAccessControl;
import eu.trentorise.smartcampus.api.manager.model.SourceAddress;

/**
 * Class that apply policy IP Access Control.
 * 
 * @author Giulia Canobbio
 *
 */
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
	 * It throws a security exception if access is denied.
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
		else{
			logger.info("Ip Access Control  policy --> DENY ");
			throw new SecurityException("DENY - " +
				" Ip Access Control policy DENIES access.");
		}
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
		
		/*String appIp_24=appIpMask(appIp)[0]; 
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
		return decision;*/
		
		boolean decision = false;

		boolean BL_or = false;
		boolean WL_or = false;

		try {
			if(BL!=null){
				for (int i = 0; i < BL.size(); i++) {
					BL_or = BL_or || IP(BL.get(i).getMask(), ip2ipbv(BL.get(i).getIp()), ip2ipbv(appIp));
				}
			}
			
			if(WL!=null){
				for (int i = 0; i < WL.size(); i++) {
					WL_or = WL_or || IP(WL.get(i).getMask(), ip2ipbv(WL.get(i).getIp()), ip2ipbv(appIp));
				}
			}
		} catch (UnknownHostException e) {
			return false;
		}

		System.out.print("BL_or=" + BL_or + "\n");
		if (noRuleMatchAction.equalsIgnoreCase("ALLOW")) {
			if ((!BL_or) || WL_or)
				decision = true;
		}
		if (noRuleMatchAction.equalsIgnoreCase("DENY")) {
			if ((!BL_or) && WL_or)
				decision = true;
		}

		return decision;
	}

	/**
	 * Return string value of a ip in bit.
	 * 
	 * @param appIp : String
	 * @return string value of ip
	 */
	/*private String[] appIpMask(String appIp){
	
		String contrary=new StringBuilder(appIp).reverse().toString();
		int endIndex = appIp.length()-contrary.indexOf('.')-1;	
		String appIp_24 =appIp.substring(0, endIndex );

		String contrary2=new StringBuilder(appIp_24).reverse().toString();
		int endIndex2 = appIp_24.length()-contrary2.indexOf('.')-1;	
		String appIp_16 =appIp.substring(0, endIndex2 );
		String[] ip=new String[]{appIp_24,appIp_16};
		return ip;
	}*/
	
	/**
	 * Retrieve int representation of ip address.
	 * 
	 * @param ip : String
	 * @return int : ip
	 * @throws UnknownHostException 
	 */
	private int ip2ipbv(String ip) throws UnknownHostException{
		/*int[] index = new int[3];
		int j = 0;
		for (int i = 0; i < ip.length(); i++) {
			if (ip.charAt(i) == '.') {
				index[j] = i;
				j = j + 1;
			}
		}
		int ip_a = Integer.parseInt(ip.substring(0, index[0]));
		int ip_b = Integer.parseInt(ip.substring(index[0] + 1, index[1]));
		int ip_c = Integer.parseInt(ip.substring(index[1] + 1, index[2]));
		int ip_d = Integer.parseInt(ip.substring(index[2] + 1));
		*/
		Inet4Address a = (Inet4Address) InetAddress.getByName(ip);
		byte[] b = a.getAddress();
		int ip_bv = ((b[0] & 0xFF) << 24) |
                 	((b[1] & 0xFF) << 16) |
                 	((b[2] & 0xFF) << 8)  |
                 	((b[3] & 0xFF) << 0);

		//int ip_bv = ip_a << 24 | ip_b << 16 | ip_c << 8 | ip_d; // concat two int of 8 bits
		return ip_bv;
	}
		
				
	/**
	 * Check if app ip is in range of ip with mask.
	 * 
	 * @param mask : int
	 * @param ip : int
	 * @param appIp : int
	 * @return boolean : true if app ip is in range, otherwise false
	 */
	private boolean IP(int mask, int ip, int appIp) {
		int min24 = 255 << 24 | 255 << 16 | 255 << 8 | 0;
		int min16 = 255 << 24 | 255 << 16 | 0 << 8 | 0;
		Boolean result = false;

		if (mask == 32 && appIp == ip) {
			result = true;
		} else if (mask == 24 && (ip & min24) < appIp && appIp < (ip | 255)) {
			result = true;
		} else if (mask == 16 && (ip & min16) < appIp && appIp < (ip | 65535)) {

			result = true;
		}
		return result;
	}

	

}
