package com.citi.olympus.permcomparator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthentication customAuthentication;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public SecurityContextRepository securityContextRepo() {
		return new HttpSessionSecurityContextRepository();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthentication);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		
		  http .antMatcher("/**").authorizeRequests()
		  .antMatchers("/login**","/error**","/permcomparator/callback**","/query**", "/oauth2**","/logout**").permitAll()
		  .anyRequest().authenticated()
		  .and().csrf().disable().cors()
		  .and()
		  .oauth2Login().loginPage("/login").and().logout().logoutUrl("/logout").logoutSuccessUrl("/logout");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/admin/lib/**",
				"/lib/**", "/query**", "/oauth2**","/logout**");
	}
}
