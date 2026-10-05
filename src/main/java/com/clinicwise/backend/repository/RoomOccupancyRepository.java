package com.clinicwise.backend.repository;

import com.clinicwise.backend.entity.RoomOccupancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomOccupancyRepository extends JpaRepository<RoomOccupancy, Integer>, JpaSpecificationExecutor<RoomOccupancy> {
    @EntityGraph(attributePaths = {"room", "employee.user"})
    @Override
    Page<RoomOccupancy> findAll(Specification<RoomOccupancy> spec, Pageable pageable);
}
