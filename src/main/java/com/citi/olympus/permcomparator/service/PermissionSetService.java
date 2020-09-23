package com.citi.olympus.permcomparator.service;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.citi.olympus.permcomparator.model.AccessToken;
import com.citi.olympus.permcomparator.repo.AccessTokenRepo;
import com.citi.olympus.permcomparator.utils.Utilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.Nullable;

@Service
public class PermissionSetService {
	
	@Autowired
	RestTemplate template;
	
	@Autowired
	AccessTokenRepo accessRepo;
	
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
	 

	@Value("${qury.objectPermission.user}")
	String user_objPerm;
	
	@Value("${qury.objectPermission.permissionSet}")
	String permission_objPerm;
	
	@Value("${qury.objectPermission.profile}")
	String profile_objPerm;
	
	@Value("${query.user.all}")
	private String userAll;

	@Value("${query.permissionSet.all}")
	private String permissionSetAll;

	@Value("${query.profile.all}")
	private String profileAll;
	
	
	public String query(String userId, String type, String id, String qry) {
		
		HttpHeaders headers = new HttpHeaders(); 
	    headers.set("Accept",MediaType.APPLICATION_JSON_VALUE);
	    AccessToken tokenObj = this.getAccessToken(userId);
	    String query = qry;
	    if(query == null) {	    	
	    	query = this.getQuery(type, id, userId);
	    }
	    headers.set("Authorization", tokenObj.getToken_type() + " "+ tokenObj.getAccess_token());
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> toRet = null;
		
		try {
			System.out.println("query= " + query);
			toRet = template.exchange(tokenObj.getInstance_url() + "/services/data/v48.0/query/?q=" + query ,HttpMethod.GET, entity, String.class);
		} catch(HttpStatusCodeException ex) {
			System.out.println("===============================");
			System.out.println(ex.getMessage() + " " + ex.getRawStatusCode() + " " + ex.getResponseBodyAsString());
			System.out.println("===============================");
		
		}
		return toRet.getBody();
		
	}
	
	public AccessToken getAccessToken(String userId) {
		System.out.println("userId: " + userId);
		//AccessToken accessToken = accessRepo.findById_token(userId);
		AccessToken toRet = null;
		Optional<AccessToken> accessToken = accessRepo.findById(userId);
		if(accessToken.isPresent()) {
			toRet = accessToken.get();
		} else {
			System.out.println("AccessToken not found for this user: " + userId);
		}
		return toRet;
	}
	
	@SuppressWarnings("unchecked")
	public String querySetupEntity(String userId, String type, String id) throws ParseException {
		
		
		JSONObject toRet = new JSONObject();
		
		String setupEntityResponse = this.query(userId, type, id, null);
		
		JSONParser parser = new JSONParser();
		JSONObject setupEntityAccess = (JSONObject) parser.parse(setupEntityResponse);
		JSONArray record = (JSONArray) setupEntityAccess.get("records");
		
		JSONArray appMenuRecord = new JSONArray();
		if(!record.isEmpty()) {
			StringBuilder str = new StringBuilder();
			for(int i=0; i<record.size(); i++) {
				JSONObject recrodItem = (JSONObject) record.get(i);
				String setupEntityId = (String) recrodItem.get("SetupEntityId");
				str.append("'"+ setupEntityId +"',");
			}
			
			String appMenuQry = this.getQuery("AppMenuItem", id, userId);
			
			appMenuQry = appMenuQry.replace("%SetupEntityId%", str.substring(0, str.length()-1));
			
			String appMenuItem = this.query(userId, "AppMenuItem", id,appMenuQry);
			System.out.println("SetupEntityId: " + str.substring(0, str.length()-1));
			
			JSONObject appMenuResponse = (JSONObject) parser.parse(appMenuItem);
			appMenuRecord = (JSONArray) appMenuResponse.get("records");
		}
		toRet.put("appMenuItem", appMenuRecord);
		
		return toRet.toString();
	}
	
	public String getQuery(String type, String id, String userId) {
		String toRet = "";
		
		System.out.println("user_objPerm = " + user_objPerm);
		System.out.println("permission_objPerm = " + permission_objPerm);
		System.out.println("profile_objPerm = " + profile_objPerm);
		
		switch(type) {
			/** Object Perm Query **/
			case "user.ObjectPerm":
				toRet = user_objPerm.replaceAll("%searchId%", id);
				break;
			case "permissionSet.ObjectPerm":
				toRet = permission_objPerm.replaceAll("%searchId%", id);
				break;
			case "profile.ObjectPerm":
				toRet = profile_objPerm.replaceAll("%searchId%", id);
				break;
				
			/** User Perm Query **/
			
			case "user.UserPerm":
				toRet = "SELECT Id " + this.getPermissionFields(userId) + " FROM PermissionSet Where Id IN (SELECT PermissionSetId from PermissionSetAssignment WHERE AssigneeId = '"+ id +"')";
				break;
			case "permissionSet.UserPerm":
				toRet = "SELECT Id " + this.getPermissionFields(userId) + " FROM PermissionSet Where Id = '"+ id +"'";
				break;
			case "profile.UserPerm":
				toRet = "SELECT Id " + this.getPermissionFields(userId) + " FROM PermissionSet Where Id = '"+ id +"'";
				break;
				
			/** PermissionSet Query **/
				
			case "user.SetupEntity":
				toRet = "SELECT SetupEntityId FROM SetupEntityAccess WHERE ParentId IN (SELECT PermissionSetId from PermissionSetAssignment WHERE AssigneeId= '"+ id +"')";
				break;
			case "permissionSet.SetupEntity":
				toRet = "SELECT SetupEntityId FROM SetupEntityAccess WHERE ParentId = '"+ id +"'";
				break;
			case "profile.SetupEntity":
				toRet = "SELECT SetupEntityId FROM SetupEntityAccess WHERE ParentId = '"+ id +"'";
				break;
			case "loggedUserPermission":
				toRet = "SELECT Id, PermissionSetId, PermissionSet.Name, PermissionSet.Label, AssigneeId, Assignee.Name FROM PermissionSetAssignment WHERE AssigneeId = '"+ userId +"'";
				break;
			case "query.userList":
					toRet = userAll;
					break;
			case "query.permissionSetList":
				toRet = permissionSetAll;
				break;
			case "query.pofileList":
				toRet = profileAll;
				break;
			case "AppMenuItem":
				toRet = "SELECT Id, Label FROM AppMenuItem WHERE applicationId IN (%SetupEntityId%)";
				break;
			case "ApexClass":
				toRet = "SELECT Id, Label, Name FROM ApexClass WHERE applicationId IN (%SetupEntityId%)"; //TODO: confirm the query
				break;
			case "ApexPage":
				toRet = "SELECT Id, Label, Name FROM ApexPage WHERE applicationId IN (%SetupEntityId%)"; //TODO: confirm the query
				break;
		}
		
		return toRet;
		
	}

public String getPermissionFields(String userId) {
		
		AccessToken tokenObj = this.getAccessToken(userId);
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

}
