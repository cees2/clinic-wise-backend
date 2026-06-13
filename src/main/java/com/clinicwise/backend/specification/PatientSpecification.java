package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.FilterCondition;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.common.list.filter.PatientsFilter;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.Gender;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientSpecification {
    public static Specification<Patient> whereFilter(PatientsFilter patientsFilter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (patientsFilter.getName() != null) {
                Join<Patient, User> patientUserJoin = root.join("user");
                Path<String> patientName = patientUserJoin.get("firstname");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getName());
                String parsedNameValue = parsedFilter.value();

                Predicate predicate = parsedFilter.condition() == FilterCondition.C ?
                        cb.like(patientName, "%" + parsedNameValue + "%") :
                        cb.equal(patientName, parsedNameValue);

                predicates.add(predicate);
            }

            if (patientsFilter.getSurname() != null) {
                Join<Patient, User> patientUserJoin = root.join("user");
                Path<String> patientLastname = patientUserJoin.get("lastname");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getSurname());
                String parsedSurnameValue = parsedFilter.value();

                Predicate predicate = parsedFilter.condition() == FilterCondition.C ?
                        cb.like(patientLastname, "%" + parsedSurnameValue + "%") :
                        cb.equal(patientLastname, parsedSurnameValue);

                predicates.add(predicate);
            }

            if(patientsFilter.getDateOfBirth() != null) {
                Join<Patient, User> patientUserJoin = root.join("user");
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
                Join<Patient, User> patientUserJoin = root.join("user");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getGender());
                String[] parsedGenderValue = parsedFilter.value().split(ParsedFilter.filterValueSeparator);
                Predicate predicate = patientUserJoin.get("gender").in(Arrays.asList(parsedGenderValue));

                predicates.add(predicate);
            }

            if(patientsFilter.getNationality() != null){
                Join<Patient, User> patientUserJoin = root.join("user");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(patientsFilter.getNationality());
                String[] parsedNationalityValue = parsedFilter.value().replace(" ", "_").split(ParsedFilter.filterValueSeparator);
                Predicate predicate = patientUserJoin.get("nationality").in(Arrays.asList(parsedNationalityValue));

                predicates.add(predicate);
            }

            return cb.and(predicates);
        };
    }
}
