package eu.trentorise.smartcampus.api.manager.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.api.manager.googleAnalytics.TrackingIDValidator;
import eu.trentorise.smartcampus.api.manager.model.User;
import eu.trentorise.smartcampus.api.manager.repository.UserRepository;

@Component
@Transactional
public class UserManager {
	
	@Autowired
	private UserRepository urepo;
	
	/**
	 * Saves Google Analytics Tracking id in user data.
	 * 
	 * @param username : String
	 * @param trackingid : String
	 * @return boolean if update is done without error then true, otherwise false.
	 */
	public boolean saveUserData(String username, String trackingid){
		//check pattern matcher for tracking id
		TrackingIDValidator tval = new TrackingIDValidator();
		boolean isValid = tval.validate(trackingid);
		if(!isValid){
			throw new IllegalArgumentException("This Tracking ID is not valid. The format is UA-XXXXX-YY.");
		}
		
		User u = urepo.findByUsername(username);
		u.setGatrackid(trackingid);
		
		User savedU = urepo.save(u);
		
		if(savedU!=null){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if tracking ID of user is already saved in db.
	 * 
	 * @param username : String
	 * @return boolean, if trackingID is already saved then true, otherwise false.
	 */
	public boolean isTrackingIDSave(String username){
		User u = urepo.findByUsername(username);
		if(u!=null){
			String trackID = u.getGatrackid();
			if(trackID!=null && !trackID.equalsIgnoreCase("")){
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Retrieve current user tracking ID.
	 * 
	 * @param username : String
	 * @return tracking ID : String
	 */
	public String getTrackingID(String username){
		User u = urepo.findByUsername(username);
		if(u!=null){
			return u.getGatrackid();
		}
		else return null;
	}

}
