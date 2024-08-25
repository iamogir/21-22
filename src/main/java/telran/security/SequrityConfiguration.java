package telran.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SequrityConfiguration {
	
	@Bean
	PasswordEncoder getEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
