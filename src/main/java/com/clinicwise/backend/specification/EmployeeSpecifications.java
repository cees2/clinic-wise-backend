package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Employee;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class EmployeeSpecifications {
    public static PredicateSpecification<Employee> hasDocumentIDOrEmailOrPhoneNumber(String documentID, String email, String phoneNumber) {
        PredicateSpecification<Employee> hasDocumentID =
                (root, builder) -> builder.equal(root.get("documentID"), documentID);
        PredicateSpecification<Employee> hasEmail =
                (root, builder) -> builder.equal(root.get("email"), email);
        PredicateSpecification<Employee> hasPhoneNumber =
                (root, builder) -> builder.equal(root.get("phoneNumber"), phoneNumber);

        return hasDocumentID.or(hasEmail).or(hasPhoneNumber);
    }
}
