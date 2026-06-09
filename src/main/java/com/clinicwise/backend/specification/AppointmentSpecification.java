package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.AppointmentsFilter;
import com.clinicwise.backend.common.list.filter.FilterCondition;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.entity.Appointment;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppointmentSpecification {
    public static Specification<Appointment> whereFilter(AppointmentsFilter appointmentsFilter){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(appointmentsFilter.getDuration() != null){
                Path<String> durationPath = root.get("duration");
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(appointmentsFilter.getDuration());
                String parsedFilterValue = parsedFilter.value();

                Predicate predicate = switch(parsedFilter.condition()){
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

            if(appointmentsFilter.getStatus() != null){
                ParsedFilter parsedFilter = ParsedFilter.parseFilter(appointmentsFilter.getStatus());
                String[] selectedStatuses = parsedFilter.value().split(ParsedFilter.filterValueSeparator);

                Predicate predicate = root.get("status").in(Arrays.asList(selectedStatuses));
                predicates.add(predicate);
            }

            return cb.and(predicates);
        };
    }
}
