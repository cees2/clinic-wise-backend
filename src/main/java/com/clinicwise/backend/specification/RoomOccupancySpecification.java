package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.RoomOccupancy;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDateTime;

public class RoomOccupancySpecification {
    public static PredicateSpecification<RoomOccupancy> overlappingDatesAndRoomOrEmployee(LocalDateTime startTime, LocalDateTime endTime, Integer roomId, Integer employeeId) {
        PredicateSpecification<RoomOccupancy> overlappingDates = (root, builder) -> {
            Predicate startsBeforeEnd = builder.lessThan(root.get("startTime"), endTime);
            Predicate endsAfterStart = builder.greaterThan(root.get("endTime"), startTime);

            return builder.and(startsBeforeEnd, endsAfterStart);
        };
        PredicateSpecification<RoomOccupancy> matchingEmployee = (root, builder) ->
                builder.equal(root.get("employee").get("id"), employeeId);
        PredicateSpecification<RoomOccupancy> matchingRoom = (root, builder) ->
                builder.equal(root.get("room").get("id"), roomId);

        return matchingEmployee.or(matchingRoom).and(overlappingDates);
    }
}
