package eu.trentorise.smartcampus.api.manager.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriValidation {
	
	private Pattern pattern;
	private Matcher matcher;
	
	private static final String URI_PATTERN = "(/[a-zA-Z]:)?(/[a-zA-Z0-9]+)+/?";
	
	/**
	 * New instance of {@link UriValidation}.
	 */
	public UriValidation(){
		pattern = Pattern.compile(URI_PATTERN);
	}
	
	/**
	 * Validate uri of a resource, checking if it matches pattern.
	 * 
	 * @param hex 
	 * 			: String to validate
	 * @return boolean value true if it matches otherwise false
	 */
	public boolean validate(final String hex){
		matcher = pattern.matcher(hex);
		return matcher.matches();
	}

}
