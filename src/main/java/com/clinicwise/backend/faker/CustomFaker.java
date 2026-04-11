package com.clinicwise.backend.faker;

import com.clinicwise.backend.faker.provider.PatientSubscriptionPlanProvider;
import com.clinicwise.backend.faker.provider.EmployeeRoleProvider;
import net.datafaker.Faker;

public class CustomFaker extends Faker {
    public PatientSubscriptionPlanProvider patientSubscriptionPlan(){
        return getProvider(PatientSubscriptionPlanProvider.class, PatientSubscriptionPlanProvider::new, this);
    }

    public EmployeeRoleProvider employeeRole(){
        return getProvider(EmployeeRoleProvider.class, EmployeeRoleProvider::new, this);
    }
}
