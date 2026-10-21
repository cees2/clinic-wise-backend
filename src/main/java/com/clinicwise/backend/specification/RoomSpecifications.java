package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Room;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecifications {
    public static PredicateSpecification<Room> hasName(String name){
        return (root, builder) -> builder.equal(root.get("name"), name);
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
