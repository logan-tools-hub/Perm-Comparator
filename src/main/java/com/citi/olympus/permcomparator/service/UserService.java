package com.citi.olympus.permcomparator.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.citi.olympus.permcomparator.model.AccessToken;
import com.citi.olympus.permcomparator.model.User;
import com.citi.olympus.permcomparator.repo.AccessTokenRepo;

@Service
public class UserService {

	@Autowired
	RestTemplate template; 

	@Autowired
	PermissionSetService permissionSetService;
	
	 
	 @Value("${spring.security.oauth2.client.provider.salesforce.token-uri}")
	 String sfTokenUrl;
	
	 @Value("${spring.security.oauth2.client.registration.salesforce.client-id}")
	 String clientId;

	 @Value("${spring.security.oauth2.client.registration.salesforce.client-secret}")
	 String clientSecret;
	 
	 @Value("${spring.security.oauth2.client.registration.salesforce.authorization-grant-type}")
	 String grantType;
	 
	 @Value("${spring.security.oauth2.client.registration.salesforce.redirect-uri}")
	 String redirectURI;
	 
	 
	 
	public User getUser(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		User user = null;
		
		AccessToken tokenResponse;
		String user_id = (String) session.getAttribute("user_id");
		tokenResponse = permissionSetService.getAccessToken(user_id);
	    if(tokenResponse != null) {
	    	user = getUserDetails(tokenResponse);
	    }
		return user;
	}
	
	public AccessToken getAccessToekn(String code, String state, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		HttpHeaders headers = new HttpHeaders(); 
	    headers.set("Accept",MediaType.APPLICATION_JSON_VALUE);
	    
		String url = sfTokenUrl + "?client_id=" + clientId +"&client_secret="+clientSecret + "&code=" + code + "&grant_type=" + grantType
				+ "&redirect_uri=http://localhost:8080/permcomparator/callback";
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<AccessToken> tokenResponse = null;
		try {
			tokenResponse = template.exchange(url,HttpMethod.POST, entity, AccessToken.class);
		} catch(HttpStatusCodeException ex) {
			System.out.println(ex.getResponseBodyAsString());
		}
		AccessToken token = tokenResponse.getBody();
		
		session.setAttribute("tokenResponse", tokenResponse.getBody());
		System.out.println(tokenResponse.getBody());
	    return token;
	}
	
	public User getUserDetails(AccessToken tokenObj) {
		
		HttpHeaders headers = new HttpHeaders(); 
	    headers.set("Accept",MediaType.APPLICATION_JSON_VALUE);
	    headers.set("Authorization", tokenObj.getToken_type() + " "+ tokenObj.getAccess_token());
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<User> toRet = null;
		
		try {
			toRet = template.exchange(tokenObj.getId_token(),HttpMethod.GET, entity, User.class);
		} catch(HttpStatusCodeException ex) {
			System.out.println("===============================");
			System.out.println(ex.getMessage() + " " + ex.getRawStatusCode() + " " + ex.getResponseBodyAsString());
			System.out.println("===============================");
		}
		
		return toRet.getBody();
	}
	
	public void revokeAccessToken(String userId) {
		
		HttpHeaders headers = new HttpHeaders(); 
	    headers.set("Accept",MediaType.APPLICATION_JSON_VALUE);
	    AccessToken tokenObj = permissionSetService.getAccessToken(userId);
	    
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> toRet = null;
		//String loginUrl = tokenObj.getId_token();
		String loginUrl = tokenObj.getInstance_url();
		loginUrl = loginUrl.split("/id/")[0];
		loginUrl = loginUrl + "/services/oauth2/revoke?token="+tokenObj.getAccess_token();
		try {
			toRet = template.exchange(loginUrl,HttpMethod.POST, entity, String.class);
			System.out.println(toRet.getStatusCode());
		} catch(HttpStatusCodeException ex) {
			System.out.println("===============================");
			System.out.println(ex.getMessage() + " " + ex.getRawStatusCode() + " " + ex.getResponseBodyAsString());
			System.out.println("===============================");
		}
		
	}
}

