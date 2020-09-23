package com.citi.olympus.permcomparator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;

@SpringBootApplication
public class PermComparatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PermComparatorApplication.class, args);
	}

}
