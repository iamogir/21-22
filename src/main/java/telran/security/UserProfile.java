package telran.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@SuppressWarnings("serial")
public class UserProfile extends User {
	
	public boolean passwordIsNotExpired;
	
	public UserProfile(String username, String password, Collection<? extends GrantedAuthority> authorities, boolean passwordIsNotExpired) {
		super(username, password, authorities);
		this.passwordIsNotExpired = passwordIsNotExpired;
	}

	

}
