package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.error.specificationError.DomainValidator;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeesDomainValidator extends DomainValidator<Employee> {
    private final UserConflictsFinder userConflictsFinder;

    public EmployeesDomainValidator(UserConflictsFinder userConflictsFinder, EntityManager entityManager) {
        super(entityManager);
        this.userConflictsFinder = userConflictsFinder;
    }

    @Override
    protected List<ValidationError> findConflictingEntities(Employee employee) {
        return userConflictsFinder.findConflicts(employee.getUser());
    }
}
