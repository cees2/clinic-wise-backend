package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.AppointmentsFilter;
import com.clinicwise.backend.common.list.filter.FilterCondition;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.enums.AppointmentStatus;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AppointmentSpecification {
    public static Specification<Appointment> whereFilter(AppointmentsFilter appointmentsFilter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(appointmentsFilter.getSearch() != null) {
                String search = appointmentsFilter.getSearch().toUpperCase();
                Path<String> additionalNote = root.get("additionalNote");
                Path<String> patientName = root.get("patient").get("user").get("firstname");
                Path<String> employeeName = root.get("employee").get("user").get("firstname");

                Predicate predicate = cb.or(
                        cb.like(cb.upper(additionalNote), '%' + search + '%'),
                        cb.like(cb.upper(patientName), '%' + search + '%'),
                        cb.like(cb.upper(employeeName), '%' + search + '%')
                );

                predicates.add(predicate);
            }

            if (appointmentsFilter.getDuration() != null) {
                Path<String> durationPath = root.get("duration");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(appointmentsFilter.getDuration());
                String parsedFilterValue = parsedFilter.value();

                Predicate predicate = switch (parsedFilter.condition()) {
                    case FilterCondition.E -> cb.equal(durationPath, parsedFilterValue);
                    case FilterCondition.NE -> cb.notEqual(durationPath, parsedFilterValue);
                    case FilterCondition.LT -> cb.lessThan(durationPath, parsedFilterValue);
                    case FilterCondition.LTE -> cb.lessThanOrEqualTo(durationPath, parsedFilterValue);
                    case FilterCondition.GT -> cb.greaterThan(durationPath, parsedFilterValue);
                    case FilterCondition.GTE -> cb.greaterThanOrEqualTo(durationPath, parsedFilterValue);
                    default -> cb.equal(durationPath, parsedFilterValue);
                };
                predicates.add(predicate);
            }

            if (appointmentsFilter.getStatus() != null) {
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(appointmentsFilter.getStatus());
                String[] selectedStatuses = parsedFilter.value().split(ParsedFilter.filterValueSeparator);

                Predicate predicate = root.get("status").in(Arrays.asList(selectedStatuses));
                predicates.add(predicate);
            }

            if (appointmentsFilter.getAdditionalNote() != null) {
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(appointmentsFilter.getAdditionalNote());
                String additionalNote = parsedFilter.value();
                Predicate predicate = parsedFilter.condition() == FilterCondition.E ?
                        cb.equal(root.get("additionalNote"), additionalNote) :
                        cb.like(root.get("additionalNote"), "%" + additionalNote + "%");

                predicates.add(predicate);
            }

            if (appointmentsFilter.getStartDate() != null) {
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(appointmentsFilter.getStartDate());
                String startDate = parsedFilter.value();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedStartDate = LocalDate.parse(startDate, formatter);

                Predicate predicate = parsedFilter.condition() == FilterCondition.LTE ?
                        cb.lessThan(root.get("startDate"), parsedStartDate.plusDays(1).atStartOfDay()) :
                        cb.greaterThanOrEqualTo(root.get("startDate"), parsedStartDate.atStartOfDay());

                predicates.add(predicate);
            }

            return cb.and(predicates);
        };
    }

    public static Specification<Appointment> findOverlapping(Appointment appointment) {
        return (root, query, cb) -> {
            LocalDateTime appointmentStartDate = appointment.getStartDate();
            LocalDateTime appointmentEndDate = appointment.getStartDate().plusMinutes(appointment.getDuration());

            Predicate overlapping = cb.and(
                    cb.lessThan(root.get("startDate"), appointmentEndDate),
                    cb.greaterThan(root.get("endDate"), appointmentStartDate)
            );
            Predicate employeeOrPatient = cb.or(
                    cb.equal(root.get("employee").get("id"), appointment.getEmployee().getId()),
                    cb.equal(root.get("patient").get("id"), appointment.getPatient().getId())
            );
            Predicate scheduled = cb.equal(root.get("status"), AppointmentStatus.SCHEDULED);
            List<Predicate> predicateList = new ArrayList<>(List.of(overlapping, employeeOrPatient, scheduled));

            if(appointment.getId() != null){
                predicateList.add(cb.notEqual(root.get("id"), appointment.getId()));
            }

            return cb.and(predicateList);
        };
    }
}
