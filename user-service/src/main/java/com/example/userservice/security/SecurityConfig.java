package com.example.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.userservice.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final UserService userService;
	private final Environment env;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.headers().frameOptions().disable();
		http.authorizeRequests().antMatchers("/actuator/**").permitAll();
		http.authorizeRequests().antMatchers("/h2/**").permitAll();
		http.authorizeRequests().antMatchers("/error/**").permitAll()
				.antMatchers("/**").hasIpAddress("127.0.0.1")
				.and()
				.addFilter(getAuthenticationFilter());
	}
	
	
	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, env);
		authenticationFilter.setAuthenticationManager(authenticationManager());
		
		return authenticationFilter;
	}

	// select pwd from users where email = ?
	// (encrypted)db_pwd == (encrypted)input_pwd 
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
		
	}
	
	
}
