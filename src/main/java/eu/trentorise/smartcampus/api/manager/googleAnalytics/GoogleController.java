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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.services.analytics.model.GaData;

import eu.trentorise.smartcampus.api.manager.model.util.ResultData;
import eu.trentorise.smartcampus.api.manager.persistence.SecurityManager;


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
				String trackingID = smanager.retrieveTrackingID();
				GaData data = auth.getUserData(code, trackingID);
				logger.info("User Data: "+data);
				
				response.setStatus(HttpServletResponse.SC_OK);
					
				
			} catch (IOException e) {
				logger.info("IOException .. Problem in reading user data.");
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		
		return "redirect:/";
	}
	
	//TODO Rest that retrieves gaData
}
