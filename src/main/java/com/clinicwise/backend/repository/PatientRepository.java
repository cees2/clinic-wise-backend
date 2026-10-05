package com.clinicwise.backend.repository;

import com.clinicwise.backend.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PatientRepository extends JpaRepository<Patient, Integer>, JpaSpecificationExecutor<Patient> {
    @Override
    @EntityGraph(attributePaths = "user")
    Page<Patient> findAll(Specification<Patient> spec, Pageable pageable);
}
