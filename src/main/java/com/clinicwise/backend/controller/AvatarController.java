package com.clinicwise.backend.controller;

import com.clinicwise.backend.service.AvatarService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avatars")
public class AvatarController {
    AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping()
    public byte[] getAvatar(@Param("token") String token){
        return avatarService.getAvatarById( token);
    }
}
