package com.clinicwise.backend.repository;

import com.clinicwise.backend.entity.RoomOccupancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomOccupancyRepository extends JpaRepository<RoomOccupancy, Integer>, JpaSpecificationExecutor<RoomOccupancy> {
}
