package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.UserRequest;
import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.Avatar;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.mapper.UserMapper;
import com.clinicwise.backend.repository.AvatarRepository;
import com.clinicwise.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Blob;

@Service
public class UserService {
    private UserRepository userRepository;
    private JWTService JWTService;
    private AvatarRepository avatarRepository;

    public UserService(UserRepository userRepository, JWTService JWTService, AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.JWTService = JWTService;
        this.avatarRepository = avatarRepository;
    }

    // todo: remove previous avatar
    @Transactional
    public UserResponse updateUser(UserRequest userRequest, MultipartFile avatar, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring("Bearer ".length());
        String username = JWTService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        UserMapper.updateUserFromRequest(userRequest, user, avatar);
        Avatar createdAvatar = user.getAvatar();

        if(createdAvatar != null) {
            avatarRepository.save(createdAvatar);
        }

//        userRepository.save(user);

        return UserMapper.toResponse(user);
    }
}
