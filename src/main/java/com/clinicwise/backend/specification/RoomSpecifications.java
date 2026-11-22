package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Room;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecifications {
    public static PredicateSpecification<Room> hasName(Room room){
        return (root, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("name"), room.getName()));

            if(room.getId() != null){
                predicates.add(builder.notEqual(root.get("id"), room.getId()));
            }

            return builder.and(predicates);
        };
    }

    public static Specification<Room> searchSelect(String search) {
        return (root, query, criteriaBuilder) -> {
            if(search == null || search.isBlank()){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(root.get("name"), "%" + search + "%");
        };
    }
}
