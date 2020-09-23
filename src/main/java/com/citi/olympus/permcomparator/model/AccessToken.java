package com.citi.olympus.permcomparator.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AccessToken {
	
	@Id
	private String id;
	private String id_token;
	private String access_token;
	private String signature;
	private String scope;
	private String instance_url;
	private String token_type;
	private String issued_at;
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getId_token() {
		return id_token;
	}
	public void setId_token(String id_token) {
		this.id_token = id_token;
	}
	public String getInstance_url() {
		return instance_url;
	}
	public void setInstance_url(String instance_url) {
		this.instance_url = instance_url;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public String getIssued_at() {
		return issued_at;
	}
	public void setIssued_at(String issued_at) {
		this.issued_at = issued_at;
	}
	
	@Override
	public String toString() {
		return "AccessToken { access_token=" + access_token + ", signature=" + signature + ", scope=" + scope 
				+ ", id_token=" + id_token + ", instance_url=" + instance_url + ", id" + id + ", token_type=" + token_type + ", issued_at=" + issued_at;
	}
	
}
