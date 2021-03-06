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
package eu.trentorise.smartcampus.api.manager.googleAnalytics;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.services.analytics.model.GaData;

import eu.trentorise.smartcampus.api.manager.model.util.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.SecurityManager;
import eu.trentorise.smartcampus.api.manager.security.CustomAuthenticationException;


/**
 * Google controller connects to google and retrieve user data.
 * 
 * @author Giulia Canobbio
 *
 */
@Controller
@RequestMapping(value = "/api/oauth/google")
public class GoogleController {
	
	/**
	 * Instance of {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(GoogleController.class);
	/**
	 * Instance of {@link GoogleAuthHelper}.
	 */
	@Autowired
	private GoogleAuthHelper auth;
	/**
	 * Instance of {@link SecurityManager}
	 */
	@Autowired
	private SecurityManager smanager;
	
	/**
	 * Service that start oauth authentication flow with Google,
	 * building a state token and login url.
	 * 
	 * @param response : instance of {@link HttpServletResponse}
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link ResultData} with google login url, 
	 * 			status (OK or NOT FOUND) and a string message : 
	 * 			"Authentication Google Login URL" if it is ok, "Problem with Google API".
	 */
	@RequestMapping(value = "/auth", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResultData socialGooglePlus(HttpServletResponse response, HttpServletRequest request) {
		logger.info("****** Google auth ******");
		
		String token = auth.getStateToken();
		String loginURL = auth.buildLoginUrl();
		
		//save in session
		request.getSession().setAttribute("state",token);
		response.setStatus(HttpServletResponse.SC_OK);
		
		if(token!=null && loginURL!=null){
			return new ResultData(loginURL, HttpServletResponse.SC_OK, "Authentication Google Login URL");
		}else{
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, "Problem with Google API");
		}
	}
	
	/**
	 * This rest web service is the one that google called after login (callback url).
	 * First it retrieve code and token that google sends back. 
	 * It checks if code and token are not null, then if token is the same as the one saved in session.
	 * If it is not then response status is UNAUTHORIZED, otherwise it retrieves user data.
	 * 
	 * Then redirects authenticated user to home page where user can access protected resources.
	 * 
	 * @param request : instance of {@link HttpServletRequest}
	 * @param response : instance of {@link HttpServletResponse}
	 * @return redirect to home page
	 */
	@RequestMapping(value = "/callback", method = RequestMethod.GET, produces = "application/json")
	public String confirmStateToken(HttpServletRequest request, HttpServletResponse response){
		
		logger.info("****** Google callback ******");
		String code = request.getParameter("code");
		String token = request.getParameter("state");
		String session_token = "";
		if(request.getSession().getAttribute("state")!=null){
			session_token = request.getSession().getAttribute("state").toString();
		}
		
		logger.info("request code: "+code);
		logger.info("request token: "+token);
		logger.info("request session token: "+session_token);
		
		//compare state token in session and state token in response of google
		//if equals return to home
		//if not error page
		if( (code==null || token==null) && (!token.equals(session_token))){
			logger.info("Error: You have to sign in!");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}else{
			try {
				//init analytics
				//String trackingID = smanager.retrieveTrackingID();
				auth.getUserAnalytics(code/*,trackingID*/);
				
				response.setStatus(HttpServletResponse.SC_OK);
					
				
			} catch (IOException e) {
				logger.info("IOException .. Problem in reading user data.");
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		
		return "redirect:/";
	}
	
	/**
	 * Retrieves event data of an api searching by label.
	 * 
	 * @param apiName : String
	 * @return instance of {@link ResultData} with google event data, 
	 * 			status (OK, FORBIDDEN or NOT FOUND) and a string message : 
	 * 			"Event data found." if it is ok, "You are not allowed" 
	 * 			or "Problem with Google Analytics.".
	 */
	@RequestMapping(value = "/eventlabel/{apiName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResultData googleEvent(@PathVariable String apiName){
		logger.info("Retrieve event from ga account.");
		try {
			String trackingID = smanager.retrieveTrackingID();
			String profileID = auth.getProfileId(trackingID);
			GaData event = auth.executeDataQueryEventLabel(profileID, apiName);
			
			return new ResultData(auth.castGaDataObject(event), 
					HttpServletResponse.SC_OK, "Event data found.");
			
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, 
					"You are not allowed.");
			
		}catch (IOException e) {
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem with Google Analytics.");
			
		}
	}
	
	/**
	 * Retrieves event data of an api searching by action.
	 * 
	 * @param apiName : String
	 * @return instance of {@link ResultData} with google event data, 
	 * 			status (OK, FORBIDDEN or NOT FOUND) and a string message : 
	 * 			"Event data found." if it is ok, "You are not allowed" 
	 * 			or "Problem with Google Analytics.".
	 */
	@RequestMapping(value = "/eventaction/{apiName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResultData googleEventAction(@PathVariable String apiName){
		logger.info("Retrieve event from ga account.");
		try {
			String trackingID = smanager.retrieveTrackingID();
			String profileID = auth.getProfileId(trackingID);
			if(profileID!=null){
				GaData event = auth.executeDataQueryEventAction(profileID, apiName);
				return new ResultData(auth.castGaDataObject(event), 
						HttpServletResponse.SC_OK, "Event data found.");
			}
			else return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem with Google Analytics. Cannot find profile id.");
			
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, 
					"You are not allowed.");
			
		}catch (IOException e) {
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem with Google Analytics.");
			
		}
	}
	
	/**
	 * Retrieves exception data of an api searching by description.
	 * 
	 * @param apiName : String
	 * @return instance of {@link ResultData} with google exception data, 
	 * 			status (OK, FORBIDDEN or NOT FOUND) and a string message : 
	 * 			"Event data found." if it is ok, "You are not allowed" 
	 * 			or "Problem with Google Analytics.".
	 */
	@RequestMapping(value = "/exception/{apiName}", method = RequestMethod.GET, 
			produces = "application/json")
	@ResponseBody
	public ResultData googleException(@PathVariable String apiName){
		logger.info("Retrieve exception from ga account.");
		try {
			String trackingID = smanager.retrieveTrackingID();
			String profileID = auth.getProfileId(trackingID);
			GaData exception = auth.executeDataQueryException(profileID, apiName);
			return new ResultData(auth.castGaDataObject(exception), 
					HttpServletResponse.SC_OK, "Exception data found.");
			
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, 
					"You are not allowed.");
			
		}catch (IOException e) {
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem with Google Analytics.");
			
		}
	}
	
	/**
	 * Retrieves all event data.
	 * 
	 * @return instance of {@link ResultData} with google event data list, 
	 * 			status (OK, FORBIDDEN or NOT FOUND) and a string message : 
	 * 			"Event data found." if it is ok, "You are not allowed" 
	 * 			or "Problem with Google Analytics.".
	 */
	@RequestMapping(value = "/event/list", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResultData googleListEvent(){
		logger.info("Retrieve event list from ga account.");
		try {
			String trackingID = smanager.retrieveTrackingID();
			String profileID = auth.getProfileId(trackingID);
			
			GaData elist = auth.executeDataQueryListEvent(profileID);
			return new ResultData(auth.castGaDataObject(elist), 
					HttpServletResponse.SC_OK, "Event data found.");
			
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, 
					"You are not allowed.");
			
		}catch (IOException e) {
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem with Google Analytics.");
			
		}
	}
	
	/**
	 * Retrieves all exception data.
	 * 
	 * @return instance of {@link ResultData} with google exception data list, 
	 * 			status (OK, FORBIDDEN or NOT FOUND) and a string message : 
	 * 			"Event data found." if it is ok, "You are not allowed" 
	 * 			or "Problem with Google Analytics.".
	 */
	@RequestMapping(value = "/exception/list", method = RequestMethod.GET, 
			produces = "application/json")
	@ResponseBody
	public ResultData googleListException(){
		logger.info("Retrieve exception list from ga account.");
		try {
			String trackingID = smanager.retrieveTrackingID();
			String profileID = auth.getProfileId(trackingID);
			
			GaData exclist = auth.executeDataQueryListException(profileID);
			return new ResultData(auth.castGaDataObject(exclist), 
					HttpServletResponse.SC_OK, "Exception data found.");
			
		} catch (CustomAuthenticationException e) {
			return new ResultData(null, HttpServletResponse.SC_FORBIDDEN, 
					"You are not allowed.");
			
		}catch (IOException e) {
			return new ResultData(null, HttpServletResponse.SC_NOT_FOUND, 
					"Problem with Google Analytics.");
			
		}
	}
	
	/**
	 * 
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link ResultData} with boolean value, 
	 * 			status (OK or NOT FOUND) and a string message : 
	 * 			"You are logged in Google Analytics." if it is ok, 
	 * 			otherwise "You have to login with Google Analytics."
	 */
	@RequestMapping(value = "/logged", method = RequestMethod.GET, 
			produces = "application/json")
	@ResponseBody
	public ResultData isLogged(HttpServletRequest request){
		logger.info("Is user logged?");

		boolean isEnabled = auth.isAnalyticsEnabled();
		if(isEnabled){
			if(request.getSession().getAttribute("state")!=null){
				return new ResultData(isEnabled, HttpServletResponse.SC_OK, 
						"You are logged in Google Analytics.");
			}
			else return new ResultData(false, HttpServletResponse.SC_NOT_FOUND, 
					"You have to login with Google Analytics.");
		}
		else return new ResultData(isEnabled, HttpServletResponse.SC_NOT_FOUND, 
				"You have to login with Google Analytics.");
		
	}
}
