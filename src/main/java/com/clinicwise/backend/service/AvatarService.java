package com.clinicwise.backend.service;

import com.clinicwise.backend.config.MyUserDetails;
import com.clinicwise.backend.config.MyUserDetailsService;
import com.clinicwise.backend.entity.Avatar;
import com.clinicwise.backend.repository.AvatarRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AvatarService {
    private AvatarRepository avatarRepository;
    private JWTService JWTService;
    private MyUserDetailsService myUserDetailsService;

    public AvatarService(AvatarRepository avatarRepository, JWTService JWTService, MyUserDetailsService myUserDetailsService) {
        this.avatarRepository = avatarRepository;
        this.JWTService = JWTService;
        this.myUserDetailsService = myUserDetailsService;
    }

    public byte[] getAvatarById(String token) {
        String username = JWTService.extractUsername(token);
        MyUserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        if (token == null || !JWTService.validateToken(token, userDetails))
            throw new IllegalArgumentException("Invalid or missing token");

        return userDetails.getUser().getAvatar().getAvatar();
    }
}
