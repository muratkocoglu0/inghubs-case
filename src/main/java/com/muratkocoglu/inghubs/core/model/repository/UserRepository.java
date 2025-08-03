package com.muratkocoglu.inghubs.core.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.muratkocoglu.inghubs.core.model.entity.LoginUser;

public interface UserRepository extends JpaRepository<LoginUser, Long> {
    
	Optional<LoginUser> findByUsername(String username);
}
