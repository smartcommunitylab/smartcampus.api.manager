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

/**
 * Model for SAML policy.
 * validate Signer : a boolean that can be set to true or false. When true, the policy validates 
 * 		the digital signature on the SAML assertion against the digital certificates
 * 		in the TrustStore named in the TrustStore element.
 * trustStore: name of the TrustStore that contains trusted X.509 certificates used to validate 
 * 		digital signatures on SAML assertions. (Endpoint)
 * 
 * @author Giulia Canobbio
 *
 */
public class SAML extends Policy{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean valSigner;
	private String truststore;
	
	/**
	 * 
	 * @return validate Signer : boolean
	 */
	public boolean isValSigner() {
		return valSigner;
	}
	/**
	 * 
	 * @param valSigner : boolean
	 */
	public void setValSigner(boolean valSigner) {
		this.valSigner = valSigner;
	}
	/**
	 * 
	 * @return trustStore : String
	 */
	public String getTruststore() {
		return truststore;
	}
	/**
	 * 
	 * @param truststore : String
	 */
	public void setTruststore(String truststore) {
		this.truststore = truststore;
	}

	
}
