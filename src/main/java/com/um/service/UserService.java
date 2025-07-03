package com.um.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.um.model.AppUser;
import com.um.repository.AppUserRepository;

@Service
public class UserService {
    @Autowired
    private AppUserRepository userRepository;

    public AppUser authenticate(String username, String password) {
        AppUser user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)&& "APPROVED".equals(user.getStatus())) {
            return user;
        }
        return null;
    }

	public void registerUser(AppUser appUser) {
		userRepository.save(appUser);		
	}
	
	public List<AppUser> findUsersByStatus(String status) {
        return userRepository.findByStatus(status);
    }
	
	public AppUser findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateStatus(Long id, String status) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(status);
            userRepository.save(user);
        });
    }
}
