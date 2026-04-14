package com.clinicwise.backend.faker.provider;

import com.clinicwise.backend.enums.EmployeeRole;
import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;

public class EmployeeRoleProvider extends AbstractProvider<Faker> {
    private final EmployeeRole[] ROLES = {
            EmployeeRole.DOCTOR,
            EmployeeRole.REGISTRATION,
            EmployeeRole.ADMIN
    };

    public EmployeeRoleProvider(Faker faker) {
        super(faker);
    }

    public EmployeeRole nextEmployeeRole() {
        return ROLES[getFaker().random().nextInt(ROLES.length)];
    }
}
