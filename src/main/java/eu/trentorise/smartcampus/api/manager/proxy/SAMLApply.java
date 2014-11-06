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

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.ValidatorSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.trentorise.smartcampus.api.manager.model.SAML;

/**
 * Class that validate a SAML response.
 * It validates the status of response and looks for Authentication Statement.
 * Then it looks for a Conditions statement and 
 * checks that the timestamps in the assertion are valid. 
 * It also checks format and confirms a Subject Confirmation was provided 
 * and contains a valid timestamps.
 * It checks the Recipient and Signature.
 * 
 * @author Giulia Canobbio
 *
 */
public class SAMLApply implements PolicyDatastoreApply{
	/**
	 * Instance of {@Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(SAMLApply.class);
	
	//global variables
	private String apiId;
	private String resourceId;
	//private String appId;
	private SAML p;
	
	private String samlAssertion;
	
	public SAMLApply(String apiId, String resourceId, /*String appId,*/ SAML p, String samlAssertion){
		this.apiId = apiId;
		this.resourceId = resourceId;
		//this.appId = appId;
		this.p = p;
		
		this.samlAssertion = samlAssertion;
		
		logger.info("apiId {}",apiId);
		logger.info("resourceId {}",resourceId);
		logger.info("p {}",p);
		logger.info("samlassertion {}",samlAssertion);
	}

	@Override
	public void apply() {
		
		decision();
	}
	
	private void decision(){
		// both api and resource id cannot be null
		if (apiId == null && resourceId == null) {
			throw new IllegalArgumentException("Api or Resource id cannot be null.");
		} else {
			
			boolean decision = SAMLDecision(samlAssertion, p.isValSigner(), apiId, resourceId);

			if (decision)
				logger.info("SAML policy --> GRANT");
			else {
				logger.info("SAML policy --> DENY");
				throw new SecurityException(
						"DENY - SAML policy DENIES access.");
			}
		}
		
	}
	
	private boolean SAMLDecision(String samlResponse, boolean validateSigner, String apiId, String resourceId) {	
	  	
		boolean result=false;
		
		if (samlResponse==null){
			throw new IllegalArgumentException(
					"SAML assertion is required.");
		}else{		
			
			byte[] decodedsamlResponse = Base64.decode(samlResponse);
			
			String decodedResponse= new String(decodedsamlResponse);
	
			try {
				DefaultBootstrap.bootstrap(); // default configuration of OpenSaml library

				ByteArrayInputStream is = new ByteArrayInputStream(decodedsamlResponse);

				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentBuilderFactory.setNamespaceAware(true);

				DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
				/* parsing returns a Document object, which represents
				 * tree of XML document.
				 * (DOM, every element is a node of tree org.w3.dom.Node.)
				 */
				Document document = docBuilder.parse(is);

				Element element = document.getDocumentElement();

				//unmarshalling to retrieve data
				UnmarshallerFactory unmarshallerFactory = Configuration
						.getUnmarshallerFactory();
				Unmarshaller unmarshaller = unmarshallerFactory
						.getUnmarshaller(element);

				XMLObject responseXmlObj = unmarshaller.unmarshall(element);

				Response response = (Response) responseXmlObj;
				
				//validate schema of response SAML 
				validateResponseAgainstSchema(response);
				
				
				String statusCode = response.getStatus().getStatusCode()
						.getValue();
				System.out.print("StatusCode:" + statusCode);

				if (statusCode.contains("status:Success") == false) {
					System.out.print("Error: User is not authenticated.");
				} else {

					/*
					 * validate assertion
					 * 1. Validating the Status - OK
					 * 2. Looking for an Authentication Statement - OK
					 * [describes a statement by the SAML authority asserting
					 * that the assertion subject was authenticated by a
					 * particular means at a particular time][the relying party
					 * may require information additional to the assertion
					 * itself in order to assess the level of confidence they
					 * can place in that assertion] 
					 * 3. Looking for a Conditions statement - OK
					 * 4. Checking that the timestamps in the assertion are valid - OK 
					 * 5. Checking that the Attribute namespace matches, if provided - NOT NEEDED
					 * 6. Miscellaneous format confirmations - OK
					 * 7. Confirming Issuer matches - NOT NEEDED
					 * 8. Confirming a Subject Confirmation was provided and contains valid timestamps - OK
					 * 9. Checking that the Audience matches, if provided - NOT NEEDED
					 * 10. Checking the Recipient - OK
					 * 11. Validating the Signature Is the response signed? Is the assertion
					 * signed? Is the correct certificate supplied in the
					 * keyinfo? NOT NEEDED
					 * 12. Checking that the Site URL Attribute contains a valid site url, if provided - NOT NEEDED
					 * 13. Looking for portal and organization id, if provided - NOT NEEDED
					 */

					//check issuer
					validateIssuer(response.getIssuer());
					
					List<Assertion> assList = response.getAssertions();
					
					//The response must contain at least one Assertion
					if(assList == null || assList.isEmpty()){
						logger.info("The response must contain at least one Assertion");
						throw new SecurityException("Invalid SAML, the response must contain at least one Assertion");
					}
					
					//validate assertion - Looking for Authentication Statement
					boolean foundValidSubject = false;
					for(Assertion assertion : assList){
						
						//Check issuer
						if(assertion.getIssuer() ==null){
							logger.info("Assertion issuer must not be null.");
							throw new SecurityException("Assertion issuer must not be null.");
						}

						// check the current timestamp against the NotBefore and  NotOnOrAfter elements in the assertion
						validateTimes(assertion);
						
						//check signature is not null
						/*if(POST && assertion.getSignature()==null){
							throw new SecurityException("Assertion signature is missing");
						}*/
						
						
						// check signatures
						if (!validateSigner) {
							logger.info("Grant: post request with SAMLAssertion or retrieve user info.");
							result = true;
							
						} else {
							
							Signature sig = response.getSignature();
							int num_firme = 0;

							// check signature response
							if (response.isSigned()) {
								num_firme++;
								result = validateSignature(sig, p.getTruststore());
							} else {
								logger.info("Signature response is not here.");
							}

							// check signature assertion
							Signature signature = assertion.getSignature();
							if (assertion.isSigned()) {
								num_firme++;
								result = validateSignature(signature, p.getTruststore());
							} else {
								logger.info("Signature assertion is not here.");
							}

							if (num_firme == 0) {
								logger.info("Error: No validation signature");
							}
						}
						
						
						//check AuthnStatements and validate the Subject accordingly
						if(assertion.getAuthnStatements() !=null && !assertion.getAuthnStatements().isEmpty()){
							Subject subject = assertion.getSubject();
							if(validateAuthenticationSubject(subject)){
								//need spIdentifier value
								//validateAudienceRestrictionCondition(assertion.getConditions(), spIdentifier);
								foundValidSubject = true;
							}
						}

					}
					
					if(!foundValidSubject){
						logger.info("The response did not contain any Authentication Statement " +
								"that matched the Subject Confirmation criteria");
						throw new SecurityException("The response did not contain any Authentication Statement " +
								"that matched the Subject Confirmation criteria");
					}

				}

			} catch (ConfigurationException e1) {
				e1.printStackTrace();
				throw new SecurityException("SAML 2.0 problem with SAML configuration.");
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				throw new SecurityException("SAML 2.0 problem in parsing SAML response.");
				
			} catch (SAXException e) {
				e.printStackTrace();
				throw new SecurityException("SAML 2.0 problem with SAML response document. " +
						"XML Document structure is wrong.");
				
			} catch (IOException e) {
				e.printStackTrace();
				throw new SecurityException("SAML 2.0 problem in reading SAML response.");
				
			} catch (UnmarshallingException e) {
				e.printStackTrace();
				throw new SecurityException("SAML 2.0 problem in unmarshalling SAML response.");
				
			} catch(java.lang.NullPointerException n){
				n.printStackTrace();
				throw new SecurityException("SAML 2.0 problem in decoding. SAML response is not in Base64.");
				
			} catch(java.lang.ClassCastException c){
				c.printStackTrace();
				throw new SecurityException("SAML problem in casting. SAML response is not 2.0.");
				
			}
		}	
		return result;
	}

	/**
	 * This function validates conditions times.
	 * 
	 * @param assertion : {@link Assertion} instance
	 */
	private static void validateTimes(Assertion assertion){
		if (assertion.getConditions().getNotBefore() != null
				&& assertion.getConditions().getNotBefore().isAfterNow()) {
			throw new SecurityException(
					"SAML 2.0 Message is outdated (too early) !");
		}

		if (assertion.getConditions().getNotOnOrAfter() != null
				&& (assertion.getConditions().getNotOnOrAfter().isBeforeNow() || assertion
						.getConditions().getNotOnOrAfter().isEqualNow())) {
			throw new SecurityException(
					"SAML 2.0 Message is outdated (too late) !");
		}
	}



	private static boolean validateSignature(Signature sig, String truststore) {
		boolean result = false;

		SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
		try {
			profileValidator.validate(sig);
		} catch (ValidationException e) {
			e.printStackTrace();
			throw new SecurityException("SAML 2.0 problem in validate signature.");
		}

		//Validate signature with info trustStore.

		//Read certificate and read public key
		FileInputStream inStream;
		try {
			//Convert String to Uri
			URI endpoint = URI.create(truststore);
			
			//Certificate file (.cer) is needed
			inStream = new FileInputStream(new File(endpoint));
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) cf
					.generateCertificate(inStream);
			// NOTE: value of cert is in response, but I cannot use it due to cast problem.
			inStream.close();
			BasicX509Credential credential = new BasicX509Credential();
			credential.setUsageType(UsageType.SIGNING);
			credential.setEntityCertificate(cert);
			SignatureValidator validator = new SignatureValidator(credential);
			validator.validate(sig);
			result = true;
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new SecurityException("SAML 2.0 problem in retrieving the certificate.");
			
		} catch (ValidationException e) {
			System.out.print("Error keys");
			e.printStackTrace();
			return false;
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new SecurityException("SAML 2.0 problem in opening the certificate.");
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new SecurityException("SAML 2.0 problem in reading the certificate.");
		}
		return result;
	}
	
	/**
	 * This function validates a SAML response.
	 * 
	 * @param response : {@link Response} instance
	 */
	private void validateResponseAgainstSchema(Response response){
		ValidatorSuite schemaValidators = org.opensaml.Configuration.getValidatorSuite("saml2-core-schema-validator");
		try {
			schemaValidators.validate(response);
		} catch (ValidationException e) {
			logger.info("Saml validation error");
			throw new SecurityException("Saml validation error");
		}
	}
	
	/**
	 * This function validates Issuer.
	 * It check that issuer is not null and its format is correct.
	 * 
	 * @param issuer : {@link Issuer} instance
	 */
	private void validateIssuer(Issuer issuer){
		if(issuer == null){
			return;
		}
		
		//Format must be nameid-format-entity
		if(issuer.getFormat()!=null
				&& !org.opensaml.saml2.core.NameIDType.ENTITY.equals(issuer.getFormat()) 
				){
			logger.info("Issuer format is not null and does not equal: {}", org.opensaml.saml2.core.NameIDType.ENTITY);
			throw new SecurityException("Issuer is not null and does not equal to NAMEID_FORMAT_ENTITY");
		}
	}
	
	/**
	 * This function validates the Subject of an Authentication Statement.
	 * 
	 * @param subject : {@link Subject} instance
	 * @return boolean value : true if authentication subject is valid, otherwise false
	 */
	private boolean validateAuthenticationSubject(Subject subject){
		if(subject.getSubjectConfirmations() == null){
			return false;
		}
		//Need to find a Bearer Subject Confirmation method
		for(SubjectConfirmation subjectConf : subject.getSubjectConfirmations()){
			if(SubjectConfirmation.METHOD_BEARER.equals(subjectConf.getMethod())){
				validateSubjectConfirmation(subjectConf.getSubjectConfirmationData());
			}
		}
		return true;
	}
	
	/**
	 * This function validates a Bearer Subject Confirmation.
	 * 
	 * @param subjectConfData : {@link SubjectConfirmationData} instance
	 */
	private void validateSubjectConfirmation(SubjectConfirmationData subjectConfData){
		if(subjectConfData == null){
			logger.info("Subject Confirmation Data of a Bearer Subject Confirmation is null");
			throw new SecurityException("Subject Confirmation Data of a Bearer Subject Confirmation is null");
		}
		
		//check timestamp
		if(subjectConfData.getNotOnOrAfter() == null || subjectConfData.getNotOnOrAfter().isAfterNow()){
			logger.info("Subject Conf Data does not contain NotOnOrAfter or it has expired.");
			throw new SecurityException("Subject Conf Data does not contain NotOnOrAfter or it has expired.");
		}
		
		//It must not contain a NotBefore timestamp
		if(subjectConfData.getNotBefore() != null){
			logger.info("Subject Conf Data must not contain a NotBefore timestamp");
			throw new SecurityException("Subject Conf Data must not contain a NotBefore timestamp");
		}

	}
}
