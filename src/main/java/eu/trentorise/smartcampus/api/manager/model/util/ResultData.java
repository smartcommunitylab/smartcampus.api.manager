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
package eu.trentorise.smartcampus.api.manager.model.util;

/**
 * Model for response data of rest web service.
 * 
 * @author Giulia Canobbio
 *
 */
public class ResultData {
	
	private Object data;
	private int status;
	private String message;
	
	/**
	 * Simple constructor
	 */
	public ResultData(){
		
	}
	
	/**
	 * Constructor with parameter.
	 * 
	 * @param data : Object
	 * @param status : int
	 * @param message : String
	 */
	public ResultData(Object data, int status, String message){
		this.data = data;
		this.status=status;
		this.message=message;
	}
	/**
	 * 
	 * @return data : Object
	 */
	public Object getData() {
		return data;
	}
	/**
	 * 
	 * @param data : Object
	 */
	public void setData(Object data) {
		this.data = data;
	}
	/**
	 * 
	 * @return status : int
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * 
	 * @param status : int
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * 
	 * @return message : String
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * 
	 * @param message : String
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
