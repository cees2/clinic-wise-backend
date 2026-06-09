package com.clinicwise.backend.common.list.filter;

import org.springframework.web.servlet.tags.Param;

public record ParsedFilter(FilterCondition condition, String value) {
    public static final String filterValueSeparator = "あ";
    public static final String filterConditionValueSeparator = "い";

    public static ParsedFilter parseFilter(String filter) {
        String[] parts = filter.split(filterConditionValueSeparator);
        return new ParsedFilter(getCondition(parts[0]), parts[1]);
    }

    private static FilterCondition getCondition(String condition){
        return switch(condition){
            case "e" -> FilterCondition.E;
            case "ne" -> FilterCondition.NE;
            case "lt" -> FilterCondition.LT;
            case "lte" -> FilterCondition.LTE;
            case "gt" -> FilterCondition.GT;
            case "gte" -> FilterCondition.GTE;
            case "c" -> FilterCondition.C;
            default -> FilterCondition.E;
        };
    }
}
