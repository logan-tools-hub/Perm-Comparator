package com.citi.olympus.permcomparator.config;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

import com.citi.olympus.permcomparator.service.PermissionSetService;

public class CustomAuthentication implements AuthenticationProvider {

	@Autowired
	PermissionSetService permissionSetService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		StringBuffer roles = new StringBuffer();

		String user_id = authentication.getName();
		System.out.println("Before Logged User Permission: " + user_id);
		String perm = permissionSetService.query(user_id, "loggedUserPermission", "Loganathan P", null); //TODO: Remove Hardcoded value or change the query with ID

		
		JSONParser parser = new JSONParser();
		JSONObject object = null;
		try {
			object = (JSONObject) parser.parse(perm);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		JSONArray records = (JSONArray) object.get("records");
		for(int i=0; i< records.size(); i++) {
			JSONObject record = (JSONObject) records.get(i);
			JSONObject permissionSet = (JSONObject) record.get("PermissionSet");
			String role = permissionSet.get("Label").toString().replaceAll(" ", "_");
			roles.append(","+ role);
		}
		return new UsernamePasswordAuthenticationToken("test", "test",
				AuthorityUtils.commaSeparatedStringToAuthorityList(roles.toString()));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
