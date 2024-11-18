package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.entity.User;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserSvc {
    private final UserRepo userRepo;

    @Autowired
    public UserSvc(UserRepo userRepository) {
        this.userRepo = userRepository;
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User getUserId(String userId) {
        return userRepo.findByUserId(userId);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
}
