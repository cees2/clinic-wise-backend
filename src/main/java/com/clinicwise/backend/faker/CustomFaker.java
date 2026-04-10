package com.clinicwise.backend.faker;

import com.clinicwise.backend.faker.provider.PatientSubscriptionPlanProvider;
import net.datafaker.Faker;

public class CustomFaker extends Faker {
    public PatientSubscriptionPlanProvider patientSubscriptionPlan(){
        return getProvider(PatientSubscriptionPlanProvider.class, PatientSubscriptionPlanProvider::new, this);
    }
}
