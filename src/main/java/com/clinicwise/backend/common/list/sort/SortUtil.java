package com.clinicwise.backend.common.list.sort;

import org.springframework.data.domain.Sort;

import java.util.*;

public final class SortUtil {
    public static Sort parseFromString(String sort, Map<String, String> sortMap) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        String[] sortParts = sort.split(",");
        List<Sort.Order> orders = new ArrayList<>();

        for (String sortPart : sortParts) {
            ParsedSort parsedSort = ParsedSort.fromString(sortPart);
            String sortPath = sortMap.get(parsedSort.sortName);

            if (sortPath == null) {
                throw new IllegalArgumentException("Invalid sort parameter: " + parsedSort.sortName);
            }

            if (parsedSort.sortDirection.equals("asc")) {
                orders.add(Sort.Order.asc(sortPath));
            } else if(parsedSort.sortDirection.equals("desc")){
                orders.add(Sort.Order.desc(sortPath));
            } else {
                throw new IllegalArgumentException("Invalid sort direction: " + parsedSort.sortDirection);
            }
        }

        return Sort.by(orders);
    }

    private record ParsedSort(String sortName, String sortDirection) {
        public static ParsedSort fromString(String sort) {
            String[] sortParts = sort.split(":");
            return new ParsedSort(sortParts[0], sortParts[1]);
        }
    }

    ;
}
