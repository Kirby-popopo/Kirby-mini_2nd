package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.entity.User;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import com.example.Kirby_mini_2nd.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileSvc {
    UserRepo userRepo;

    @Autowired
    public ProfileSvc(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    public String updateProfile(String id, MultipartFile file, String bio, String gender){
        User updateUser = userRepo.findById(id).get();

        String savedFileImage = FileUtil.SaveFileImage(file);

        updateUser.setDescription(bio);
        updateUser.setGender(gender);
        updateUser.setProfileImage(savedFileImage);

        try {
            userRepo.save(updateUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "UpdateSuccess";
    };



}
