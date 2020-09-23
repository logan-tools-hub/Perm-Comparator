package com.citi.olympus.permcomparator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer{
	
	/*
	 * @Bean AdminView adminView() { return new AdminView(); }
	 */
	
	@Bean
	CustomAuthentication customAuthentication() {
		return new CustomAuthentication();
	}
	
	@Bean
	//@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	
	
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}
}
