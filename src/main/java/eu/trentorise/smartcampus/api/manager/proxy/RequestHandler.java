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

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.HttpRequestHandler;

import eu.trentorise.smartcampus.api.manager.model.Api;
import eu.trentorise.smartcampus.api.manager.persistence.PersistenceManager;

/**
 * Handle request on api.
 * 
 * @author Giulia Canobbio
 *
 */
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
	 * @param request : instance of {@link HttpServletRequest}
	 * @param response : instance of {@link HttpServletResponse}
	 * @throws ServletException
	 * @throws IOException
	 */
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		//check headers data
		logger.info("Request headers: {}",request.getHeaderNames());
		
		//retrieves id of api and resource if exists
		//logger.info("Request parameters: {}",request.getParameterNames());
		
		//retrieves body
		/*StringBuilder buffer = new StringBuilder();
		try{
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			logger.info("Body: {}",buffer.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		String basepath = request.getContextPath();
		
		//retrieve api
		List<Api> apiList = apiManager.getApiByBasePath(basepath);
		if(apiList!=null && apiList.size()>0){
			logger.info("Found api: ", apiList.get(0).getName());
			apiId = apiList.get(0).getId();
			
			//retrieve ids resource
			if(apiList.get(0).getResource()!=null && apiList.get(0).getResource().size()>0){
				for(int i=0;i<apiList.get(0).getResource().size();i++){
					resourceIds.add(apiList.get(0).getResource().get(i).getId());
				}
				
			}
		}
		
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
	
	
}
