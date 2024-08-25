package telran.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.accounting.repo.UserAccountRepository;

@Service
public class CustomWebSecurity {
	
	@Autowired
	UserAccountRepository repo;
	
	public boolean checkOwner(String login) {
		
		return repo.findById(login).orElse(null) !=  null;
	}

}
