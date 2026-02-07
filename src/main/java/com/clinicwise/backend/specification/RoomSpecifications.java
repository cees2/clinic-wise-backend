package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Room;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class RoomSpecifications {
    public static PredicateSpecification<Room> hasName(String name){
        return (root, builder) -> builder.equal(root.get("name"), name);
    }
}
