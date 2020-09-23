package com.citi.olympus.permcomparator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.citi.olympus.permcomparator.model.AccessToken;




@Repository
public interface AccessTokenRepo extends JpaRepository<AccessToken, String>{
	//AccessToken findById_token(String id_token);
}
