package com.citi.olympus.permcomparator.model;

public class PermissionResponse {

	private String objectPermission; 
	private String userPermission;
	private String setupEntityPermission;
		
	public String getObjectPermission() {
		return objectPermission;
	}
	
	public void setObjectPermission(String objectPermission) {
		this.objectPermission = objectPermission;
	}
	
	public String getUserPermission() {
		return userPermission;
	}
	
	public void setUserPermission(String userPermission) {
		this.userPermission = userPermission;
	}
	
	public String getSetupEntityPermission() {
		return setupEntityPermission;
	}
	
	public void setSetupEntityPermission(String setupEntityPermission) {
		this.setupEntityPermission = setupEntityPermission;
	}
	
}
