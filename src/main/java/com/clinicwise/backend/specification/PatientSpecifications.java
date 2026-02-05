package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Patient;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class PatientSpecifications {
    public static PredicateSpecification<Patient> hasDocumentIdOrPhoneNumber(String documentId, String phoneNumber) {
        PredicateSpecification<Patient> hasDocumentId =
                (root, builder) -> builder.equal(root.get("documentId"), documentId);
        PredicateSpecification<Patient> hasPhoneNumber =
                (root, builder) -> builder.equal(root.get("phoneNumber"), phoneNumber);

        return hasDocumentId.or(hasPhoneNumber);
    }
}
