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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.springframework.web.HttpRequestHandler;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Handle request on api.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
public class RequestHandler{
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	/**
	 * Autowired instance of {@link PersistenceManager}
	 */
	@Autowired
	private PersistenceManager apiManager;
	
	//global variables
	/**
	 * Global variable, api id
	 */
	private String apiId;
	/**
	 * Global variable, list of resource id
	 */
	private List<String> resourceIds;
	
	/**
	 * Method that retrieves basepath of api and api resource.
	 * Then it set two global variables.
	 * It suppose that the request is for example:
	 * http(s)://proxy/api_basepath
	 * 
	 * @param url : String, url of api
	 * @param request : instance of {@link HttpServletRequest}
	 * @return HashMap with String key, value.
	 * 			ApiID - key for api id
	 * 			Resource(i) - for resource id, where i is index
	 * 			<Header name> - for header
	 */
	public HashMap<String, String> handleUrl(String url, HttpServletRequest request){
		HashMap<String, String> map = new HashMap<String, String>();
		
		String basepath = splitBasePath(url);
		
		if (basepath != null && !basepath.equalsIgnoreCase("")) {
			// retrieve api
			try {
				List<Api> apiList = apiManager.getApiByBasePath(basepath);
				if (apiList != null && apiList.size() > 0) {
					logger.info("Found api: ", apiList.get(0).getName());
					apiId = apiList.get(0).getId();

					List<Resource> rlist = apiList.get(0).getResource();
					// retrieve ids resource
					if (rlist != null && rlist.size() > 0) {
						for (int i = 0; i < rlist.size(); i++) {
							resourceIds.add(rlist.get(i).getId());
						}

					}
				}
			} catch (NullPointerException n) {
				logger.info("There is no api for this basepath {}.",
						basepath);
			}

		} else {
			logger.info("There is some problems with split method.");
		}
		
		map.put("ApiID", apiId);
		for(int i=0;i<resourceIds.size();i++){
			map.put("Resource"+i, resourceIds.get(i));
		}
		
		// retrieves headers
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {

			String headerName = headerNames.nextElement();

			Enumeration<String> headers = request.getHeaders(headerName);
			while (headers.hasMoreElements()) {
				String headerValue = headers.nextElement();
				map.put(headerName, headerValue);
			}

		}
		return map;
	}

	/**
	 * Method that retrieves basepath of api and api resource.
	 * Then it set two global variables.
	 * It suppose that the request is for example:
	 * http(s)://proxy/api_basepath
	 * 
	 * @param request : instance of {@link HttpServletRequest}
	 * @return HashMap with String key, value.
	 * 			ApiID - key for api id
	 * 			Resource(i) - for resource id, where i is index
	 * 			<Header name> - for header
	 */
	public HashMap<String, String> handleRequest(HttpServletRequest request) {

		HashMap<String, String> map = new HashMap<String, String>();
		
		// init resource ids list
		resourceIds = new ArrayList<String>();

		String requestUri = request.getRequestURI();
		String[] slist = requestUri.split("/", 3);
		for (int i = 0; i < slist.length; i++) {
			logger.info("index: {}", i);
			logger.info("value: {} --", slist[i]);
		}
		String basepath = "/" + slist[2];
		logger.info("(a)Api Basepath: {}", basepath);
		// retrieve api
		try {
			logger.info("1a {}: ", basepath);
			List<Api> apiList = apiManager.getApiByBasePath(basepath);
			logger.info("2a {}: ", basepath);
			if (apiList != null && apiList.size() > 0) {
				logger.info("(a)Found api: ", apiList.get(0).getName());
				apiId = apiList.get(0).getId();

				List<Resource> rlist = apiList.get(0).getResource();
				// retrieve ids resource
				if (rlist != null && rlist.size() > 0) {
					for (int i = 0; i < rlist.size(); i++) {
						resourceIds.add(rlist.get(i).getId());
					}

				}
			}
		} catch (NullPointerException n) {
			logger.info("There is no api for this basepath {}.", basepath);
		}
		
		map.put("ApiID", apiId);
		for(int i=0;i<resourceIds.size();i++){
			map.put("Resource"+i, resourceIds.get(i));
		}
		
		// retrieves headers
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {

			String headerName = headerNames.nextElement();

			Enumeration<String> headers = request.getHeaders(headerName);
			while (headers.hasMoreElements()) {
				String headerValue = headers.nextElement();
				map.put(headerName, headerValue);
			}

		}
		
		return map;

	}
	
	//getter
	
	/**
	 * 
	 * @return String api id
	 */
	public String getApiId() {
		return apiId;
	}
	
	/**
	 * 
	 * @return String list of resource id
	 */
	public List<String> getResourceIds() {
		return resourceIds;
	}
	
	/**
	 * Method that retrieves basepath from a string in body.
	 * Problem: basepath has = character at the end of the string.
	 * 
	 * @param url : String
	 * @return basepath of api 
	 */
	private String splitBasePath(String url){
		//String
		//url sample http(s)://proxy/api_basepath
		String[] slist = url.split("//");
		
		//print data
		for(int i=0;i<slist.length;i++){
			logger.info("String pieces - index: {}",i);
			logger.info("String pieces - value: {} --",slist[i]);
		}
		
		//to avoid = after basepath
		String basepath = slist[1].substring(slist[1].indexOf("/"),slist[1].indexOf("="));
		logger.info("Base path: {}", basepath);
		
		return basepath;
	}
	
	
}
