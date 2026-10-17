package com.clinicwise.backend.repository;

import com.clinicwise.backend.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {
    @EntityGraph(attributePaths = {"patient.user", "employee.user"})
    List<Appointment> findTop5ByStartDateAfterOrderByStartDateAsc(LocalDateTime now);

    @Override
    @EntityGraph(attributePaths = {"patient.user", "employee.user"})
    Page<Appointment> findAll(Specification<Appointment> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"patient.user", "employee.user"})
    List<Appointment> findAll(Specification<Appointment> spec);

    @Override
    @EntityGraph(attributePaths = {"patient.user", "employee.user"})
    Optional<Appointment> findById(Integer id);
}
