package com.clinicwise.backend.mapper;

import net.datafaker.Faker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MapperUtils {
    private static final Faker faker = new Faker();

    public static LocalDate randomDate60YearsTo20YearsAgo() {
        LocalDate start = LocalDate.now().minusYears(60);
        LocalDate end = LocalDate.now().minusYears(20);

        return getRandomDate(start, end);
    }

    public static LocalDate randomDate7YearsTo1WeekAgo() {
        LocalDate start = LocalDate.now().minusYears(7);
        LocalDate end = LocalDate.now().minusWeeks(1);

        return getRandomDate(start, end);
    }

    public static LocalDateTime randomFutureDate60Days(){
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(60);

        return getRandomDateTime(getRandomDate(start, end));
    }

    private static LocalDate getRandomDate(LocalDate start, LocalDate end) {
        Instant startInstant = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant randomInstant = faker.timeAndDate().between(startInstant, endInstant);

        return randomInstant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static LocalDateTime getRandomDateTime(LocalDate date) {
        int randomHour = faker.number().numberBetween(6, 20);
        int randomMinute = faker.number().numberBetween(0, 3) * 15;

        return date.atTime(randomHour, randomMinute);
    }
}
