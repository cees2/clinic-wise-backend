package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.RoomsOccupancyFilter;
import com.clinicwise.backend.entity.RoomOccupancy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoomOccupancySpecification {
    public static Specification<RoomOccupancy> whereFilter(RoomsOccupancyFilter roomsOccupancyFilter){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(roomsOccupancyFilter.getDateFilter() != null){
                Path<LocalDateTime> startTime = root.get("startTime");
                Path<LocalDateTime> endTime = root.get("endTime");
                LocalDate day = LocalDate.parse(roomsOccupancyFilter.getDateFilter());
                LocalDateTime startDay = day.atStartOfDay();
                LocalDateTime endDay = startDay.plusDays(1);
                Predicate predicate = cb.and(
                        cb.lessThan(startTime, endDay),
                        cb.greaterThan(endTime, startDay)
                );

                predicates.add(predicate);
            }

            if(roomsOccupancyFilter.getRoomFilter() != null && !roomsOccupancyFilter.getRoomFilter().isBlank()){
                List<Integer> roomIds = Arrays.stream(roomsOccupancyFilter.getRoomFilter().split(","))
                        .map(String::trim)
                        .mapToInt(Integer::parseInt)
                        .boxed()
                        .collect(Collectors.toList());
                Predicate predicate = root.get("room").get("id").in(roomIds);


                predicates.add(predicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

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
