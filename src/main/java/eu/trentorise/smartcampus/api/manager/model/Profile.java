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
package eu.trentorise.smartcampus.api.manager.model;

import java.io.Serializable;

/**
 * Class profile which will become Blob Object for Users table.
 * Having field:
 * imgAvatar,
 * name,
 * surname,
 * birthday,
 * address,
 * country,
 * hobby,
 * interest.
 * 
 * @author Giulia Canobbio
 *
 */
public class Profile implements Serializable{
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private String imgAvatar;//url image
	private String name;
	private String surname;
	private String birthday;
	private String address;
	private String country;
	private String hobby;
	private String interest;
	private String phone;
	private String mobile;
	
	/**
	 * New {@link Profile} instance with params.
	 * 
	 * @param name
	 * 			: String
	 * @param surname
	 * 			: String
	 * @param hobby
	 * 			: String
	 * @param birthday
	 * 			: String
	 */
	public Profile(String name, String surname, String hobby, String birthday){
		this.name=name;
		this.surname=surname;
		this.hobby=hobby;
		this.birthday=birthday;
	}

	/**
	 * Get name.
	 * 
	 * @return String, name of profile
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name.
	 * 
	 * @param name 
	 * 			: String 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get surname.
	 * 
	 * @return String surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Set surname.
	 * 
	 * @param surname
	 * 			: String
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * Get hobby.
	 * 
	 * @return String, hobby of profile
	 */
	public String getHobby() {
		return hobby;
	}

	/**
	 * Set hobby.
	 * 
	 * @param hobby
	 * 			: String
	 */
	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	/**
	 * Get birthday.
	 * 
	 * @return String, birthday of profile
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * Set birthday.
	 * 
	 * @param birthday
	 * 			: String
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * Get image of profile.
	 * 
	 * @return String, url avatar profile
	 */
	public String getImgAvatar() {
		return imgAvatar;
	}

	/**
	 * Set image of profile.
	 * 
	 * @param imgAvatar 
	 * 			: String, url of user avatar
	 */
	public void setImgAvatar(String imgAvatar) {
		this.imgAvatar = imgAvatar;
	}

	/**
	 * Get address.
	 * 
	 * @return String address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Set address.
	 * 
	 * @param address
	 * 			: String
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Get country.
	 * 
	 * @return String country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Set country.
	 * 
	 * @param country
	 * 			: String
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Get interest.
	 * 
	 * @return String interest
	 */
	public String getInterest() {
		return interest;
	}

	/**
	 * Set interest. 
	 * @param interest
	 * 				: String
	 */
	public void setInterest(String interest) {
		this.interest = interest;
	}

	/**
	 * Get phone number.
	 * 
	 * @return String phone of profile
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Set phone number.
	 * 
	 * @param phone
	 * 				: String
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Get mobile phone number.
	 * 
	 * @return String mobile phone of profile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * Set mobile phone number.
	 * 
	 * @param mobile
	 * 			: String, mobile phone number
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
