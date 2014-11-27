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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * A helper class for Google's OAuth2 authentication API.
 * 
 * @version 20130224
 * @author Matyas Danter
 * 
 * Modified by Giulia Canobbio.
 */
@Service("googleHelper")
public final class GoogleAuthHelper {

	private final String CLIENT_ID;
	private final String CLIENT_SECRET;
	private final String CALLBACK_URI;

	/*
	 *  google authentication constants
	 */
	
	//scope
	//First: read-only access to the Analytics API
	//Second: write access to the Analytics API
	//Third: view and manage user permissions for Analytics accounts
	private static final Iterable<String> SCOPE = Arrays
			.asList(
					"https://www.googleapis.com/auth/analytics;https://www.googleapis.com/auth/analytics.manage.users;https://www.googleapis.com/auth/userinfo.email;https://www.googleapis.com/auth/analytics.readonly"
					.split(";"));
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private String stateToken;
	private final GoogleAuthorizationCodeFlow flow;
	
	private String APPLICATION_NAME;
	/**
	 * Instance of {@link Analytics}
	 */
	private static Analytics analytics;

	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT
	 * ID, SECRET, and SCOPE.
	 * 
	 * @param client_id : String
	 * @param client_secret : String
	 * @param callback_uri : String
	 */
	public GoogleAuthHelper(String client_id, String client_secret, String callback_uri) {
		
		CLIENT_ID=client_id;
		CLIENT_SECRET=client_secret;
		CALLBACK_URI=callback_uri;
		
		System.out.println("CLIENT ID: "+CLIENT_ID);
		System.out.println("CLIENT SECRET: "+CLIENT_SECRET);
		System.out.println("CALLBACK: "+CALLBACK_URI);
		
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, (Collection<String>) SCOPE).build();

		generateStateToken();

	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope.
	 * 
	 * @return String, login URL
	 */
	public String buildLoginUrl() {

		final GoogleAuthorizationCodeRequestUrl url = flow
				.newAuthorizationUrl();

		return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
	}

	/**
	 * Generates a secure state token.
	 */
	private void generateStateToken() {

		SecureRandom sr1 = new SecureRandom();

		stateToken = "google;" + sr1.nextInt();

	}

	/**
	 * 
	 * @return state token : String
	 */
	public String getStateToken() {
		return stateToken;
	}

	/**
	 * Expects an Authentication Code, and makes an authenticated request for
	 * the user's profile information.
	 * 
	 * @param authCode : String, authentication code provided by google
	 * @throws IOException
	 */
	public void getUserAnalytics(final String authCode, String trackingID) throws IOException{

		final GoogleTokenResponse response = flow.newTokenRequest(authCode)
				.setRedirectUri(CALLBACK_URI).execute();
		final Credential credential = flow.createAndStoreCredential(response,
				null);
		
		//init analytics
		analytics = new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
		        APPLICATION_NAME).build();
		
		String profileID = getProfileId(analytics, trackingID);
		System.out.println("Profile ID: "+profileID);
		GaData event = executeDataQueryEvent(profileID, "Geocoding");
		printGaData(event);

	}
	
	private static String getProfileId(Analytics analytics, String trackingID)
			throws IOException {
		// profile id from tracking ID
		String[] s = trackingID.split("-");// tracking ID: UA-XXXXXX-YY where
											// XXXXXX is profile ID
		String myaccountId = s[1];

		String profileId = null;

		// Query accounts collection
		Accounts accounts = analytics.management().accounts().list().execute();

		if (accounts.getItems().isEmpty()) {
			System.err.println("No accounts found");
		} else {
			for (int i = 0; i < accounts.getItems().size(); i++) {
				String accountId = accounts.getItems().get(i).getId();
				System.out.println("Account id: " + accountId);

				if (accountId.equalsIgnoreCase(myaccountId)) {
					// Query webproperties collection.
					Webproperties webproperties = analytics.management()
							.webproperties().list(accountId).execute();

					if (webproperties.getItems().isEmpty()) {
						System.err.println("No Webproperties found");
					} else {

						for (int j = 0; j < webproperties.getItems().size(); j++) {

							String webpropertyId = webproperties.getItems()
									.get(j).getId();
							System.out.println("Web property id: "
									+ webpropertyId);

							if (webpropertyId.equalsIgnoreCase(trackingID)) {

								// Query profiles collection.
								Profiles profiles = analytics.management()
										.profiles()
										.list(accountId, webpropertyId)
										.execute();

								if (profiles.getItems().isEmpty()) {
									System.err.println("No profiles found");
								} else {

									profileId = profiles.getItems().get(0)
											.getId();
								}

							}
						}

					}
				}
			}
		}
		return profileId;
	}


	/**
	 * Retrieves event of an api from user's google Analytics account.
	 * 
	 * @param profileID : String
	 * @param apiName : String
	 * @return instance of {@link GaData}
	 * @throws IOException
	 */
	public GaData executeDataQueryEvent(String profileID, String apiName)
			throws IOException {
		
		// Today date
		Date today = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dToday = format.format(today);

		// Today -1 month
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date beforeToday = cal.getTime();
		String before = format.format(beforeToday);
		
		//TODO
		if (analytics != null) {

			GaData dataEvent = analytics.data()
					.ga()
					.get("ga:" + profileID, // Table Id. ga: + profile id.
							before, // Start date.
							dToday, // End date.
							"ga:totalEvents")
					// Metrics.
					.setDimensions("ga:eventLabel,ga:eventAction")
					.setSort("-ga:eventLabel")
					.setFilters("ga:eventLabel=~^" + apiName + ".*")
					.setMaxResults(150).execute();
			
			printGaData(dataEvent);

			return dataEvent;
		} else
			return null;
	}
	
	/**
	 * Retrieves exception of an api from user's google Analytics account.
	 * 
	 * @param profileID : String
	 * @param apiName : String
	 * @return instance of {@link GaData}
	 * @throws IOException
	 */
	public GaData executeDataQueryException(String profileID, String apiName)
			throws IOException {
				
		//Today date
		Date today = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dToday = format.format(today);
		
		//Today -1 month
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date beforeToday = cal.getTime();
		String before = format.format(beforeToday);
		
		// TODO
		if (analytics != null) {
			GaData dataExc = analytics.data()
					.ga()
					.get("ga:" + profileID, // Table Id. ga: + profile id.
							before, // Start date.
							dToday, // End date.
							"ga:exceptions")
					// Metrics.
					.setDimensions("ga:exceptionDescription")
					.setSort("-ga:exceptionDescription")
					.setFilters("ga:exceptionDescription=~^" + apiName + ".*")
					.setMaxResults(150).execute();
			printGaData(dataExc);
			
			return dataExc;
		} else
			return null;
	}

	/**
	 * Function that prints Google Analytics data.
	 * 
	 * @param results : instance of {@link GaData}
	 */
	private static void printGaData(GaData results) {
		System.out.println("printing results for profile: "
				+ results.getProfileInfo().getProfileName());

		if (results.getRows() == null || results.getRows().isEmpty()) {
			System.out.println("No results Found.");
		} else {

			// Print column headers.
			for (ColumnHeaders header : results.getColumnHeaders()) {
				System.out.printf("%30s", header.getName());
			}
			System.out.println();

			// Print actual data.
			for (List<String> row : results.getRows()) {
				for (String column : row) {
					System.out.printf("%30s", column);
				}
				System.out.println();
			}

			System.out.println();
		}
	}
}