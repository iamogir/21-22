package telran.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class AuthorizationConfiguration {
	
	@Autowired
	CustomWebSecurity customWebSecurity;
	
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http.httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
		
		http.addFilterBefore(new ExpiredPasswordFilter(), BasicAuthenticationFilter.class);
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.POST, "/account/register", "/account/register/", "/models", "/model")
				.permitAll()
				.requestMatchers(HttpMethod.PUT, "/account/revoke/", "account/activate").hasRole("ADMIN")
				.requestMatchers("/account/user/*/role/*").hasAnyRole("ADMIN", "MODERATOR", "SUPERADMIN")
				.requestMatchers(HttpMethod.PUT, "/account/user/*")
//				.access("@CustomWebSecurity.checkOwner(#login)") -> v5
//				.access(new WebExpressionAuthorizationManager("@CustomWebSecurity.checkOwner(#login)")) -> v6.1
				.access((auth, context) -> new AuthorizationDecision(customWebSecurity.checkOwner(context.getVariables().get("login")))) // -> 6.2
				.requestMatchers(HttpMethod.GET, "/account/*/{login}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
				.requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
				
				
				.requestMatchers("/records/*/*").hasRole("TECHNICIAN")
				.requestMatchers("/driver/add", "/car/return", "/model/cars", "/car/rent").hasRole("CLERK")
				.requestMatchers("/driver/cars", "/drivers/car", "/driver").hasAnyRole("CLERK", "DRIVER")
				.requestMatchers("/drivers/active", "/models/*").hasRole("STATIST")
				.requestMatchers("/*/remove", "/car/add", "/model/add").hasRole("MANAGER")
				.requestMatchers("/account/login", "/account/password", "/car/**").authenticated()
				.anyRequest().denyAll());
	
		return http.build();
		
	}

}
// account/roles/{role}/user/{user} -> account/roles/*/user/* [only one smth]
//account/roles/user/{role}/{user}, account/roles/user/{role}, account/roles/user, account/roles/user/{role}/{user}/aaaa -> account/roles/user/** 
