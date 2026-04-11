package com.clinicwise.backend.mapper;

import net.datafaker.Faker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class MapperUtils {
    public static LocalDate randomDate60YearsTo20YearsAgo(Faker faker) {
        LocalDate start = LocalDate.now().minusYears(60);
        LocalDate end = LocalDate.now().minusYears(20);

        return getRandomDate(faker, start, end);
    }

    public static LocalDate randomDate7YearsTo1WeekAgo(Faker faker) {
        LocalDate start = LocalDate.now().minusYears(7);
        LocalDate end = LocalDate.now().minusWeeks(1);

        return getRandomDate(faker, start, end);
    }

    private static LocalDate getRandomDate(Faker faker, LocalDate start, LocalDate end) {
        Instant startInstant = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant randomInstant = faker.timeAndDate().between(startInstant, endInstant);

        return randomInstant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
