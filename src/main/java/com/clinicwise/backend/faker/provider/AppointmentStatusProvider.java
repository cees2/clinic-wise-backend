package com.clinicwise.backend.faker.provider;

import com.clinicwise.backend.enums.AppointmentStatus;
import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;

public class AppointmentStatusProvider extends AbstractProvider<Faker> {
    private final AppointmentStatus[] STATUS = {
            AppointmentStatus.CANCELLED,
            AppointmentStatus.COMPLETED,
            AppointmentStatus.SCHEDULED
    };

    public AppointmentStatusProvider(Faker faker) {
        super(faker);
    }

    public AppointmentStatus nextAppointmentStatus() {
        return STATUS[getFaker().random().nextInt(STATUS.length)];
    }
}