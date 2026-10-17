package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.FilterCondition;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.common.list.filter.PatientsFilter;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientSpecification {
    public static Specification<Patient> whereFilter(PatientsFilter patientsFilter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Patient, User> patientUserJoin = root.join("user");
            Path<String> patientName = patientUserJoin.get("firstname");
            Path<String> patientLastname = patientUserJoin.get("lastname");

            if(patientsFilter.getSearch() != null ){
                String searchUpperCase = patientsFilter.getSearch().toUpperCase();
                String pattern = "%" + searchUpperCase + "%";
                Path<String> documentId = patientUserJoin.get("documentId");
                Expression<String> fullName = cb.concat(cb.concat(patientName, " "), patientLastname);
                Predicate predicate = cb.or(
                    cb.like(cb.upper(fullName), pattern),
                    cb.like(cb.upper(documentId), pattern)
                );

                predicates.add(predicate);
            }

            if (patientsFilter.getName() != null) {
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getName());
                String parsedNameValue = parsedFilter.value().toUpperCase();
                Expression<String> patientNameUpperCase = cb.upper(patientName);

                Predicate predicate = parsedFilter.condition() == FilterCondition.C ?
                        cb.like(patientNameUpperCase, "%" + parsedNameValue + "%") :
                        cb.equal(patientNameUpperCase, parsedNameValue);

                predicates.add(predicate);
            }

            if (patientsFilter.getSurname() != null) {
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getSurname());
                String parsedSurnameValue = parsedFilter.value().toUpperCase();
                Expression<String> patientLastnameUpperCase = cb.upper(patientLastname);

                Predicate predicate = parsedFilter.condition() == FilterCondition.C ?
                        cb.like(patientLastnameUpperCase, "%" + parsedSurnameValue + "%") :
                        cb.equal(patientLastnameUpperCase, parsedSurnameValue);

                predicates.add(predicate);
            }

            if(patientsFilter.getDateOfBirth() != null) {
                Path<LocalDate> patientDateOfBirth = patientUserJoin.get("dateOfBirth");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getDateOfBirth());
                String startDate = parsedFilter.value();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedDateOfBirth = LocalDate.parse(startDate, formatter);

                Predicate predicate = parsedFilter.condition() == FilterCondition.LTE ?
                        cb.lessThanOrEqualTo(patientDateOfBirth, parsedDateOfBirth) :
                        cb.greaterThanOrEqualTo(patientDateOfBirth, parsedDateOfBirth);

                predicates.add(predicate);
            }

            if(patientsFilter.getGender() != null){
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getGender());
                List<Gender> parsedGenderValue = Arrays.stream(parsedFilter.value().split(ParsedFilter.filterValueSeparator))
                        .map(gender -> Gender.valueOf(gender.trim().toUpperCase()))
                        .toList();
                Predicate predicate = patientUserJoin.get("gender").in(parsedGenderValue);

                predicates.add(predicate);
            }

            if(patientsFilter.getNationality() != null){
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getNationality());
                List<Country> parsedNationalityValue = Arrays.stream(parsedFilter.value().split(ParsedFilter.filterValueSeparator))
                        .map(nationality -> Country.valueOf(nationality.trim().replace(" ", "_").toUpperCase()))
                        .toList();
                Predicate predicate = patientUserJoin.get("nationality").in(parsedNationalityValue);

                predicates.add(predicate);
            }

            return cb.and(predicates);
        };
    }

    //    TODO: TESTS
    public static Specification<Patient> searchSelect(String search) {
        return (root, query, criteriaBuilder) -> {
            if(search == null || search.isEmpty()){
                return criteriaBuilder.conjunction();
            }

            Join<Patient, User> patientUserJoin = root.join("user");
            Expression<String> fullname = criteriaBuilder.concat(patientUserJoin.get("firstname"), patientUserJoin.get("lastname"));

            return criteriaBuilder.like(criteriaBuilder.upper(fullname), "%" + search.toUpperCase() + "%");
        };
    }
}
