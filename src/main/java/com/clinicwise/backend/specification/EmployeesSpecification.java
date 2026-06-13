package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.EmployeesFilter;
import com.clinicwise.backend.common.list.filter.FilterCondition;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeesSpecification {
    public static Specification<Employee> whereFilter(EmployeesFilter employeesFilter){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(employeesFilter.getName() != null){
                Join<Employee, User> employeeUserJoin = root.join("user");
                Path<String> firstname = employeeUserJoin.get("firstname");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(employeesFilter.getName());
                String filterValue = parsedFilter.value();
                Predicate predicate = parsedFilter.condition() == FilterCondition.C ?
                        cb.like(firstname, "%" + filterValue + "%") :
                        cb.equal(firstname, filterValue);

                predicates.add(predicate);
            }

            if(employeesFilter.getLastname() != null){
                Join<Employee, User> employeeUserJoin = root.join("user");
                Path<String> lastname = employeeUserJoin.get("lastname");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(employeesFilter.getLastname());
                String filterValue = parsedFilter.value();
                Predicate predicate = parsedFilter.condition() == FilterCondition.C ?
                        cb.like(lastname, "%" + filterValue + "%") :
                        cb.equal(lastname, filterValue);

                predicates.add(predicate);
            }

            if(employeesFilter.getGender() != null){
                Join<Employee, User> employeeUserJoin = root.join("user");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(employeesFilter.getGender());
                String[] filterValue = parsedFilter.value().split(ParsedFilter.filterValueSeparator);
                Predicate predicate = employeeUserJoin.get("gender").in(Arrays.asList(filterValue));

                predicates.add(predicate);
            }

            if(employeesFilter.getStartDate() != null){
                Path<LocalDate> startDate = root.get("startDate");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(employeesFilter.getStartDate());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDateValue = LocalDate.parse(parsedFilter.value(), formatter);
                Predicate predicate = parsedFilter.condition() == FilterCondition.GTE ?
                        cb.greaterThanOrEqualTo(startDate, startDateValue) :
                        cb.lessThanOrEqualTo(startDate, startDateValue);

                predicates.add(predicate);
            }

            if(employeesFilter.getDateOfBirth() != null) {
                Join<Employee, User> employeeUserJoin = root.join("user");
                Path<LocalDate> employeeDateOfBirth = employeeUserJoin.get("dateOfBirth");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(employeesFilter.getDateOfBirth());
                String startDate = parsedFilter.value();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedDateOfBirth = LocalDate.parse(startDate, formatter);

                Predicate predicate = parsedFilter.condition() == FilterCondition.LTE ?
                        cb.lessThanOrEqualTo(employeeDateOfBirth, parsedDateOfBirth) :
                        cb.greaterThanOrEqualTo(employeeDateOfBirth, parsedDateOfBirth);

                predicates.add(predicate);
            }

            if(employeesFilter.getNationality() != null){
                Join<Employee, User> employeeUserJoin = root.join("user");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(employeesFilter.getNationality());
                String[] parsedNationalities = parsedFilter.value().replace(" ", "_").split(ParsedFilter.filterValueSeparator);
                Predicate predicate = employeeUserJoin.get("nationality").in(parsedNationalities);

                predicates.add(predicate);
            }

            return cb.and(predicates);
        };
    }
}
