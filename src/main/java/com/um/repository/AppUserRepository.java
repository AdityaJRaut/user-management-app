package com.um.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.um.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    List<AppUser> findByStatus(String status);
}
