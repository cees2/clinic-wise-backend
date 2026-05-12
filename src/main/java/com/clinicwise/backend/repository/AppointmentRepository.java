package com.clinicwise.backend.repository;

import com.clinicwise.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findTop5ByStartDateAfterOrderByStartDateAsc(LocalDateTime now);
}
