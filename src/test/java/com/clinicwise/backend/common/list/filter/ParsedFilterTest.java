package com.clinicwise.backend.common.list.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParsedFilterTest {
    String exampleFilterValue = "e" + ParsedFilter.filterConditionValueSeparator + "Damian"
;
    @Test
    @DisplayName("Should return a ParsedFilter object")
    void shouldReturnParsedFilterObject() {
        ParsedFilter parsedFilter = ParsedFilter.parseFilter(exampleFilterValue);

        assertInstanceOf(ParsedFilter.class, parsedFilter);
        assertNotNull(parsedFilter);
    }

    @Test
    @DisplayName("Should return condition 'e' and value 'Damian' for given mock string")
    void shouldReturnConditionAndValue() {
        ParsedFilter parsedFilter = ParsedFilter.parseFilter(exampleFilterValue);

        assertEquals(FilterCondition.E, parsedFilter.condition());
        assertEquals("Damian", parsedFilter.value());
    }
}