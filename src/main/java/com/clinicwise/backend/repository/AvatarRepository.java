package com.clinicwise.backend.repository;

import com.clinicwise.backend.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {
    Avatar findByUserId(int userId);
}
