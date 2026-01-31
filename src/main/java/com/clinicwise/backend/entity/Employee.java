package com.clinicwise.backend.entity;

import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "name", length = 25, nullable = false)
    private String name;
    @Column(name = "surname", length = 25, nullable = false)
    private String surname;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    @Column(name = "nationality", length = 15, nullable = false)
    private String nationality;
    @Column(name = "document_id", length = 6, unique = true, nullable = false)
    private String documentID;
    @Column(name = "address", nullable = false, length = 50)
    private String address;
    @Column(name = "phone_number", length = 15, unique = true, nullable = false)
    private String phoneNumber;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EmployeeRole role;
}
