package com.citi.olympus.permcomparator.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.citi.olympus.permcomparator.model.AccessToken;
import com.citi.olympus.permcomparator.utils.Utilities;

@Service
public class ProfilePermissionSet {

	@Autowired
	RestTemplate template;
	
	@Autowired
	PermissionSetService permissionSetService;
	
	public String getPermissionFields(String userId) {
		
		AccessToken tokenObj = permissionSetService.getAccessToken(userId);
		HttpHeaders headers = new HttpHeaders(); 
	    headers.set("Accept",MediaType.APPLICATION_JSON_VALUE);
	    headers.set("Authorization", tokenObj.getToken_type() + " "+ tokenObj.getAccess_token());
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> toRet = null;
		
		try {
			toRet = template.exchange(tokenObj.getInstance_url() + "/services/data/v48.0/sobjects/PermissionSet/describe",HttpMethod.GET, entity, String.class);
		} catch(HttpStatusCodeException ex) {
			System.out.println("===============================");
			System.out.println(ex.getMessage() + " " + ex.getRawStatusCode() + " " + ex.getResponseBodyAsString());
			System.out.println("===============================");
		}
		
		JSONObject obj = (JSONObject) Utilities.parseStringToObject(toRet.getBody());
		JSONArray fieldsArr = (JSONArray) obj.get("fields");
		
		StringBuilder permFiels = new StringBuilder(); 
		for(int i=0; i<fieldsArr.size(); i++) {
			JSONObject field = (JSONObject) fieldsArr.get(i);
			
			String name = (String) field.get("name");
			if(name.startsWith("Permissions")) {
				permFiels.append(", "+ name);
			}
		}
		
		return permFiels.toString();
	}
	
	
	public String etUserPermissionSets(String userId, String type, String id) {
		
		
		
		return "";
	}
}
