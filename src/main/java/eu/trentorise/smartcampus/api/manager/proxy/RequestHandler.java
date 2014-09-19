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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.model.App;
import eu.trentorise.smartcampus.api.manager.model.ObjectInMemory;
import eu.trentorise.smartcampus.api.manager.model.RequestHandlerObject;
import eu.trentorise.smartcampus.api.manager.model.Resource;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;
import eu.trentorise.smartcampus.api.security.CustomAuthenticationException;

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
	 * Then it set two global variables.
	 * It suppose that the request is for example:
	 * http(s)://proxy/api_basepath/resource_path
	 * 
	 * @param url : String, url of api
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link RequestHandlerObject} with api id, resource id
	 * 			and a map of request headers.
	 */
	public RequestHandlerObject handleUrl(String url, HttpServletRequest request){
		
		String apiId = null, resourceId = null;
		RequestHandlerObject result = new RequestHandlerObject();
		
		String path = splitUrl(url);
		
		if (path != null && !path.equalsIgnoreCase("")) {
			//retrieve api id and resource from static resource
			if(all!=null && all.size()>0){
				if(all.containsKey(path)){
					ObjectInMemory obj = all.get(path);
					apiId = obj.getApiId();
					resourceId = obj.getResourceId();
				}else{
					throw new IllegalArgumentException("This path is not correct.");
				}

			}
			if(apiId==null && resourceId==null){
				throw new IllegalArgumentException("There is no api and resource for this path.");
			}

		} else {
			throw new IllegalArgumentException("There is some problems with split method.");
		}
		
		// retrieves headers
		Map<String, String> map = new HashMap<String, String>();
		if (request != null) {
			Enumeration<String> headerNames = request.getHeaderNames();

			while (headerNames.hasMoreElements()) {

				String headerName = headerNames.nextElement();

				Enumeration<String> headers = request.getHeaders(headerName);
				while (headers.hasMoreElements()) {
					String headerValue = headers.nextElement();
					map.put(headerName, headerValue);
				}

			}
		}
		
		logger.info("api id: {} ",apiId);
		logger.info("resource id: {} ", resourceId);
		
		//set result
		result.setApiId(apiId);
		result.setResourceId(resourceId);
		result.setHeaders(map);
		
		return result;
	}

	/**
	 * Method that retrieves basepath of api and api resource.
	 * It suppose that the request is for example:
	 * http(s)://proxy/api_basepath/resource_path
	 * 
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link RequestHandlerObject} with api id, resource id
	 * 			and a map of request headers.
	 * @throws CustomAuthenticationException 
	 */
	public RequestHandlerObject handleRequest(HttpServletRequest request) throws CustomAuthenticationException {

		if(request!=null){
		
		String apiId = null, resourceId = null, appId=null;
		RequestHandlerObject result = new RequestHandlerObject();

		String requestUri = request.getRequestURI();
		String[] slist = requestUri.split("/", 3);
		
		String path;
		if(slist[2].indexOf("/")==0){
			path = slist[2];
		}else{
			path = "/"+ slist[2];
		}
		logger.info("(a)Api Basepath: {}", path);
		
		//retrieve api id and resource from static resource
		if(all!=null && all.size()>0){
			if(all.containsKey(path)){
				ObjectInMemory obj = all.get(path);
				apiId = obj.getApiId();
				resourceId = obj.getResourceId();
			}else{
				retrieveUrlFromMemory(path);
				ObjectInMemory obj = all.get(path);
				apiId = obj.getApiId();
				resourceId = obj.getResourceId();
			}
		}
		if(apiId==null && resourceId==null){
			throw new IllegalArgumentException("There is no api and resource for this path {}.");
		}
		
		// retrieves headers
		Map<String, String> map = new HashMap<String, String>();
		if (request != null) {
			Enumeration<String> headerNames = request.getHeaderNames();

			while (headerNames.hasMoreElements()) {

				String headerName = headerNames.nextElement();

				Enumeration<String> headers = request.getHeaders(headerName);
				while (headers.hasMoreElements()) {
					String headerValue = headers.nextElement();
					//do not save in headers appId
					if(!headerName.equalsIgnoreCase("appId")){
						map.put(headerName, headerValue);
					}else{
						//retrieve appId from headers
						appId = headerValue;
					}
				}

			}
		}
		
		//check that appId retrieved from request is correct
		if(appId!=null){
			App app = apiManager.getAppById(appId);
			
			if(app==null){
				throw new IllegalArgumentException("App with this id does not exist.");
			}
			
		}
		
		logger.info("api id: {} ",apiId);
		logger.info("resource id: {} ", resourceId);
		logger.info("app id: {} ",appId);

		// set result
		result.setApiId(apiId);
		result.setResourceId(resourceId);
		result.setAppId(appId);
		result.setHeaders(map);
		
		return result;
		}
		else
			return null;

	}
	
	/**
	 * Method that retrieves basepath of api, app id and api resource.
	 * It suppose that the request is for example:
	 * http(s)://proxy/api_basepath/resource_path
	 * 
	 * @param appId : String
	 * @param request : instance of {@link HttpServletRequest}
	 * @return instance of {@link RequestHandlerObject} with api id, resource id, app id
	 * 			and a map of request headers.
	 */
	public RequestHandlerObject handleRequestWithAppId(String appId, HttpServletRequest request) {

		String apiId = null, resourceId = null;
		RequestHandlerObject result = new RequestHandlerObject();

		String requestUri = request.getRequestURI();
		String[] slist = requestUri.split("/", 3);

		String path;
		if(slist[2].indexOf("/")==0){
			path = slist[2];
		}else{
			path = "/"+ slist[2];
		}
		logger.info("(a)Api Basepath: {}", path);
		
		//retrieve api id and resource from static resource
		if(all!=null && all.size()>0){
			if(all.containsKey(path)){
				ObjectInMemory obj = all.get(path);
				apiId = obj.getApiId();
				resourceId = obj.getResourceId();
			}else{
				throw new IllegalArgumentException("There is no api and resource for this path in memory.");
			}
		}
		if(apiId==null && resourceId==null){
			throw new IllegalArgumentException("There is no api and resource for this path.");
		}
		
		// retrieves headers
		Map<String, String> map = new HashMap<String, String>();
		if (request != null) {
			Enumeration<String> headerNames = request.getHeaderNames();

			while (headerNames.hasMoreElements()) {

				String headerName = headerNames.nextElement();

				Enumeration<String> headers = request.getHeaders(headerName);
				while (headers.hasMoreElements()) {
					String headerValue = headers.nextElement();
					map.put(headerName, headerValue);
				}

			}
		}
		
		logger.info("api id: {} ",apiId);
		logger.info("resource id: {} ", resourceId);

		// set result
		result.setApiId(apiId);
		result.setResourceId(resourceId);
		result.setAppId(appId);
		result.setHeaders(map);
		
		return result;

	}
	
	/**
	 * Method that retrieves basepath of api, app id and api resource.
	 * It suppose that the url is for example:
	 * http(s)://proxy/api_basepath/resource_path
	 * 
	 * @param appId : String
	 * @param url : String
	 * @return instance of {@link RequestHandlerObject} with api id, resource id, app id
	 * 			and a map of request headers.
	 */
	public RequestHandlerObject handleRequestWithAppId(String appId, String url) {
		
		String apiId = null, resourceId = null;
		RequestHandlerObject result = new RequestHandlerObject();

		String path = splitUrl(url);
		logger.info("(a)Api Basepath: {}", path);
		
		//retrieve api id and resource from static resource
		if(all!=null && all.size()>0){
			if(all.containsKey(path)){
				ObjectInMemory obj = all.get(path);
				apiId = obj.getApiId();
				resourceId = obj.getResourceId();
			}else{
				throw new IllegalArgumentException("There is no api and resource for this path in memory.");
			}
		}
		if(apiId==null && resourceId==null){
			throw new IllegalArgumentException("There is no api and resource for this path.");
		}
		
		logger.info("api id: {} ",apiId);
		logger.info("resource id: {} ", resourceId);

		// set result
		result.setApiId(apiId);
		result.setResourceId(resourceId);
		result.setAppId(appId);
		
		return result;

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
									if (basepath.equalsIgnoreCase(basepath
											+ uri)) {
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
