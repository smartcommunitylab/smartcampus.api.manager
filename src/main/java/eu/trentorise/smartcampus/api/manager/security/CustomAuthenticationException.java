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
package eu.trentorise.smartcampus.api.manager.security;

import javax.security.sasl.AuthenticationException;

/**
 * Exception that system throw when a user access
 * protected data.
 * 
 * @author smartcampus
 *
 */
public class CustomAuthenticationException extends AuthenticationException{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	public CustomAuthenticationException(final String message){
		super(message);
	}
	
	public CustomAuthenticationException(final String message, final Throwable cause){
		super(message,cause);
	}

}
