package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.User;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class UserSpecifications {
    public static PredicateSpecification<User> hasDocumentIDOrEmailOrPhoneNumberOrUserName(String documentId, String email, String phoneNumber, String username) {
        PredicateSpecification<User> hasDocumentID =
                (root, builder) -> builder.equal(root.get("documentId"), documentId);
        PredicateSpecification<User> hasEmail =
                (root, builder) -> builder.equal(root.get("email"), email);
        PredicateSpecification<User> hasPhoneNumber =
                (root, builder) -> builder.equal(root.get("phoneNumber"), phoneNumber);
        PredicateSpecification<User> hasUsername =
                (root, builder) -> builder.equal(root.get("username"), username);

        return hasDocumentID.or(hasEmail).or(hasPhoneNumber);
    }
}
