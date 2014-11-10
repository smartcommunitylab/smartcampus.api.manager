package eu.trentorise.smartcampus.api.manager.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	 * @return boolean if update is done without error, otherwise false.
	 */
	public boolean saveUserData(String username, String trackingid){
		//TODO check pattern matcher UA-XXXXXXXX-X for tracking id
		User u = urepo.findByUsername(username);
		u.setGatrackid(trackingid);
		
		User savedU = urepo.save(u);
		
		if(savedU!=null){
			return true;
		}
		
		return false;
	}

}
