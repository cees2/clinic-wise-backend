package com.clinicwise.backend.entity;

import com.clinicwise.backend.enums.AuthorityType;
import jakarta.persistence.*;

@Entity
@Table(name = "authorities")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    AuthorityType authority;
    @Column(name = "username")
    String username;
}
