package com.citi.olympus.permcomparator.model;


public class User {

	private String id;
	private String asserted_user;
	private String user_id;
	private String organization_id;
	private String username;
	private String nick_name;
	private String display_name;
	private String email;
	private String email_verified;
	private String first_name;
	private String last_name;
	private String timezone;
    private Photos photos;
    
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAsserted_user() {
		return asserted_user;
	}
	public void setAsserted_user(String asserted_user) {
		this.asserted_user = asserted_user;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail_verified() {
		return email_verified;
	}
	public void setEmail_verified(String email_verified) {
		this.email_verified = email_verified;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public Photos getPhotos() {
		return photos;
	}
	public void setPhotos(Photos photos) {
		this.photos = photos;
	}
      
    
    
}

