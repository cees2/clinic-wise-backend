package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> findConflicting(User user) {
        return (root, query, cb) -> {
            Predicate sameData = cb.or(
                    cb.equal(root.get("phoneNumber"), user.getPhoneNumber()),
                    cb.equal(root.get("documentId"), user.getDocumentId()),
                    cb.equal(root.get("username"), user.getUsername())
            );

            if(user.getId() == null) return sameData;

            return cb.and(sameData, cb.notEqual(root.get("id"), user.getId()));
        };
    }
}
