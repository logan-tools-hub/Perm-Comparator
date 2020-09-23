package com.citi.olympus.permcomparator.entity;

import javax.persistence.Id;

public class AccessTokenEntity {

	@Id
	private String id_token;
	private String id;
	private String access_token;
	private String signature;
	private String scope;
	private String instance_url;
	private String token_type;
	private String issued_at;
	
	
}
