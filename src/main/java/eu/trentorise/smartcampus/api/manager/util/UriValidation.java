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
package eu.trentorise.smartcampus.api.manager.util;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uri resource validation.
 * 
 * @author Giulia Canobbio
 *
 */
public class UriValidation {
	
	private Pattern pattern;
	private Matcher matcher;
	
	private static final String URI_PATTERN = "(/[a-zA-Z{}]:)?(/[a-zA-Z0-9{}]+)+/?";
	
	/**
	 * New instance of {@link UriValidation}.
	 */
	public UriValidation(){
		pattern = Pattern.compile(URI_PATTERN);
	}
	
	/**
	 * Validate uri of a resource, checking if it matches pattern.
	 * 
	 * @param hex : String to validate
	 * @return boolean value true if it matches otherwise false
	 */
	public boolean validate(final String hex){
		matcher = pattern.matcher(hex);
		boolean val = matcher.matches();
		if(val){
			return graphValidate(hex);
		}
		return val;
	}
	
	/**
	 * Suppose that there is only one '{' and '}'.
	 * Check that '{' and '}' make a balanced string.
	 * 
	 * @param hex : String to validate
	 * @return boolean value
	 */
	public boolean graphValidate(final String hex){
		Stack<String> st = new Stack<String>();
		for(char ch : hex.toCharArray()){
			if(ch == '{'){
				st.push(""+ch);
			}
			if(ch == '}'){
				try{
					st.pop();
				}catch(EmptyStackException e){
					return false;
				}
			}
		}
		if(st.empty()){
			return true;
		}
		return false;
	}

}
