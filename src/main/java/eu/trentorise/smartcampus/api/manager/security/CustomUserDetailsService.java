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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.model.User;
import eu.trentorise.smartcampus.api.manager.repository.UserRepository;

/**
 * Spring security,
 * retrieve user data.
 * 
 * @author Giulia Canobbio
 *
 */
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService{
	/**
	 * Instance of {@link Logger}
	 */
	private Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

	/**
	 * Instance of {@link UserRepository}.
	 */
	@Autowired
	private UserRepository urepo;

	/**
	 * Check if user exists in database and if username and password are correct,
	 * return a {@link UserDetails} instance.
	 */
	@Override
	public UserDetails loadUserByUsername(String arg0)
			throws UsernameNotFoundException {
		
		logger.info("Custom User Details, user: {}",arg0);
		
		final User domainUser = urepo.findByUsername(arg0);
		logger.info(" user found: {}",domainUser);
		if (domainUser == null){
			logger.info("Exception");
			throw new UsernameNotFoundException(arg0);
		}
		
		return new UserDetails() {
			
			/**
			 * Check if user is enabled
			 */
			@Override
			public boolean isEnabled() {
				int enabled = domainUser.getEnabled();
				if(enabled==0){
					return false;
				}
				else{
					return true;
				}
			}
			
			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}
			
			@Override
			public boolean isAccountNonLocked() {
				return true;
			}
			
			@Override
			public boolean isAccountNonExpired() {
				return true;
			}
			
			/**
			 * Get username
			 */
			@Override
			public String getUsername() {
				return domainUser.getUsername();
			}
			
			/**
			 * Get password
			 */
			@Override
			public String getPassword() {
				return domainUser.getPassword();
			}
			
			/**
			 * Find user role in database and put them in authority
			 */
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
				
				//roles: PROVIDER user
				roles.add(new SimpleGrantedAuthority(domainUser.getRole()));
				
				return roles;
			}
		};
	}

}
