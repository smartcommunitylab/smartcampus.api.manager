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

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.model.util.ObjectInMemory;
import eu.trentorise.smartcampus.api.manager.model.util.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.manager.util.PatternMatcher;

/**
 * Handle request on api.
 * 
 * @author Giulia Canobbio
 *
 */
@Component
@Transactional
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
	/**
	 * Instance of {@link Environment}.
	 */
	@Autowired
	private Environment env;
	/**
	 * Instance of {@link ObjectInMemory}.
	 */
	private static Map<String,ObjectInMemory> all;
	
	/**
	 * Static memory
	 */
	@PostConstruct
	private void initMemory(){
		logger.info("MEMORY");
		all = new HashMap<String, ObjectInMemory>();
		//retrieves all basepath+uri and apiId and resourceId from db
		List<Api> apilist = apiManager.listApi();
		
		if(apilist!=null && apilist.size()>0){
			for(int i=0;i<apilist.size();i++){
				String apiId = apilist.get(i).getId();
				String basepath = apilist.get(i).getBasePath();

				// save in object
				ObjectInMemory m1 = new ObjectInMemory();
				m1.setApiId(apiId);
				all.put(basepath, m1);

				//get resource
				List<Resource> rlist = apilist.get(i).getResource();
				if(rlist!=null && rlist.size()>0){
					for(int j=0;j<rlist.size();j++){
						String rId = rlist.get(j).getId();
						String uri = rlist.get(j).getUri();
						
						//save in object
						ObjectInMemory m = new ObjectInMemory();
						m.setApiId(apiId);
						m.setResourceId(rId);
						all.put(basepath+uri, m);
					}
				}
			}
		}
	}

	/**
	 * Method that retrieves basepath of api and api resource.
	 * It suppose that the request is for example:
	 * http(s)://context-path/api_basepath/resource_path
	 * 
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link RequestHandlerObject} with api id, resource id
	 * 			and a map of request headers.
	 */
	public RequestHandlerObject handleRequest(HttpServletRequest request){

		if (request != null) {

			String apiId = null, resourceId = null, appId = null;
			RequestHandlerObject result = new RequestHandlerObject();

			String requestUri = request.getRequestURI();
			String encodedUri;
			try {//TODO spring utf-8 encoding
				logger.info("Try encoding");
				encodedUri = new String(requestUri.getBytes("UTF-8"),"ASCII");
				logger.info("{}",encodedUri);
			} catch (UnsupportedEncodingException e) {
				logger.info("Problem in encoding");
				e.printStackTrace();
			}
			String[] slist = requestUri.split("/", 3);

			String path;
			if (slist[2].indexOf("/") == 0) {
				path = slist[2];
			} else {
				path = "/" + slist[2];
			}
			logger.info("(a)Api Basepath: {}", path);

			// retrieve api id and resource from static resource
			if (all != null && all.size() > 0) {
				
				//TODO pattern matcher
				logger.info("Match pattern....");
				Iterator<Entry<String, ObjectInMemory>> it = all.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String, ObjectInMemory> pairs = 
							(Map.Entry<String, ObjectInMemory>) it.next();
					boolean match = new PatternMatcher(path, pairs.getKey()).compute();
					if(match){
						logger.info("Api id {}",pairs.getValue().getApiId());
						logger.info("Resource id {}",pairs.getValue().getResourceId());
					}
				}
				logger.info("Match pattern.... END");
				
				if (all.containsKey(path)) {
					ObjectInMemory obj = all.get(path);
					apiId = obj.getApiId();
					resourceId = obj.getResourceId();
				} else {
					retrieveUrlFromMemory(path);
					ObjectInMemory obj = all.get(path);
					apiId = obj.getApiId();
					resourceId = obj.getResourceId();
				}
			}
			
			if (apiId == null && resourceId == null) {
				throw new IllegalArgumentException(
						"There is no api and resource for this path {}.");
			}

			// retrieves headers
			Map<String, String> map = new HashMap<String, String>();
			if (request != null) {
				Enumeration<String> headerNames = request.getHeaderNames();

				while (headerNames.hasMoreElements()) {

					String headerName = headerNames.nextElement();

					Enumeration<String> headers = request
							.getHeaders(headerName);
					while (headers.hasMoreElements()) {
						String headerValue = headers.nextElement();
						// do not save in headers appId
						if (!headerName.equalsIgnoreCase("appId")) {
							//logger.info("Add in my header: {}",headerName);
							map.put(headerName, headerValue);
						} else {
							// retrieve appId from headers
							logger.info("App id found: {}",headerValue);
							appId = headerValue;
						}
					}

				}
			}
			// check if ipaddress exists in headers
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
				map.put("x-forwarded-for", ipAddress);
			}
			
			//check if token exists in headers
			/*String token = request.getHeader("token");
			if(token!=null){
				map.put("token", token);
			}*/

			// check that appId retrieved from request is correct
			if (appId != null) {
				App app = apiManager.getAppById(appId);

				if (app == null) {
					throw new IllegalArgumentException(
							"App with this id does not exist.");
				}

			}

			logger.info("api id: {} ", apiId);
			logger.info("resource id: {} ", resourceId);
			logger.info("app id: {} ", appId);
			logger.info("IP address: {} ", ipAddress);

			// set result
			result.setApiId(apiId);
			result.setResourceId(resourceId);
			result.setAppId(appId);
			result.setHeaders(map);

			return result;
		} else
			return null;

	}
	
	/**
	 * Method that retrieves basepath of api and api resource.
	 * It suppose that the request is for example:
	 * http(s)://proxy/api_basepath/resource_path
	 * Used only for test.
	 * 
	 * @param request : String, ex. http(s)://proxy/api_basepath/resource_path
	 * @param appId : String
	 * @param appSecret : String
	 * @param ipAddress : String
	 * @return instance of {@link RequestHandlerObject} with api id, resource id
	 * 			and a map of request headers.
	 */
	public RequestHandlerObject handleRequest(String request,String appId, String appSecret, String ipAddress){

		if (request != null) {

			String apiId = null, resourceId = null;
			RequestHandlerObject result = new RequestHandlerObject();

			String path =  splitUrl(request);
			logger.info("(a)Api Basepath: {}", path);

			// retrieve api id and resource from static resource
			if (all != null && all.size() > 0) {
				if (all.containsKey(path)) {
					ObjectInMemory obj = all.get(path);
					apiId = obj.getApiId();
					resourceId = obj.getResourceId();
				} else {
					retrieveUrlFromMemory(path);
					ObjectInMemory obj = all.get(path);
					apiId = obj.getApiId();
					resourceId = obj.getResourceId();
				}
			}
			if (apiId == null && resourceId == null) {
				throw new IllegalArgumentException(
						"There is no api and resource for this path {}.");
			}

			//map
			Map<String, String> map = new HashMap<String, String>();
			//map: appId
			if (appId != null) {
				map.put("appId", appId);		
			}
			//map: appSecret
			if (appSecret != null) {
				map.put("appSecret", appSecret);		
			}
			
			//map:ipAddress
			map.put("X-FORWARDED-FOR", ipAddress);

			// check that appId retrieved from request is correct
			if (appId != null) {
				App app = apiManager.getAppById(appId);

				if (app == null) {
					throw new IllegalArgumentException(
							"App with this id does not exist.");
				}

			}

			logger.info("api id: {} ", apiId);
			logger.info("resource id: {} ", resourceId);
			logger.info("app id: {} ", appId);
			logger.info("IP address: {} ", ipAddress);

			// set result
			result.setApiId(apiId);
			result.setResourceId(resourceId);
			result.setAppId(appId);
			result.setHeaders(map);

			return result;
		} else
			return null;

	}
	
	/**
	 * Method that retrieves basepath from a string in body.
	 * Problem: basepath has = character at the end of the string.
	 * 
	 * @param url : String
	 * @return basepath of api 
	 */
	private String splitUrl(String url){
		//String
		//url sample http(s)://proxy/api_basepath/resource_uri
		String[] slist = url.split("//");
		
		//to avoid = after basepath
		String subUrl;
		if(slist[1].contains("=")){
			subUrl = slist[1].substring(slist[1].indexOf("/"),slist[1].indexOf("="));
		}else{
			subUrl = slist[1].substring(slist[1].indexOf("/"),slist[1].length());
		}
		
		return subUrl;
	}
	
	/**
	 * Memory is built when server is started.
	 * Therefore new elements are not available.
	 * This function searches for api/resource in db when they are not in 
	 * static memory.
	 * If it does not find element, then an IllegalArgumentException is thrown.
	 * 
	 * @param path : String
	 */
	private void retrieveUrlFromMemory(String path){
		
		if(all!=null && all.size()>0){
			boolean found = false;
			
			//if(!all.containsKey(path)){
				
				//search in db - if found add to memory else error
				List<Api> apilist = apiManager.listApi();
				
				
				if(apilist!=null && apilist.size()>0){
					for(int i=0;i<apilist.size();i++){
						String apiId = apilist.get(i).getId();
						String basepath = apilist.get(i).getBasePath();

						// save in object
						if(basepath.equalsIgnoreCase(path)){
							logger.info("Found api");
							ObjectInMemory m1 = new ObjectInMemory();
							m1.setApiId(apiId);
							all.put(basepath, m1);
							found = true;
						}

						//get resource
						if (!found) {
							List<Resource> rlist = apilist.get(i).getResource();
							if (rlist != null && rlist.size() > 0) {
								for (int j = 0; j < rlist.size(); j++) {
									String rId = rlist.get(j).getId();
									String uri = rlist.get(j).getUri();

									// save in object
									if (path.equalsIgnoreCase(basepath
											+ uri)) {
										logger.info("Found resource");
										ObjectInMemory m = new ObjectInMemory();
										m.setApiId(apiId);
										m.setResourceId(rId);
										all.put(basepath + uri, m);
										found = true;
									}
								}
							}
						}
					}
				}
			//}
			
			if(!found){
				throw new IllegalArgumentException("There is no api and resource " +
						"for this path in memory.");
				
			}
		}
	}
	
	
}
