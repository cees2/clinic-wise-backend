package com.clinicwise.backend.faker.provider;

import com.clinicwise.backend.enums.PatientSubscriptionPlan;
import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;

public class PatientSubscriptionPlanProvider extends AbstractProvider<Faker> {
    private final PatientSubscriptionPlan[] PLANS = {
            PatientSubscriptionPlan.BASIC,
            PatientSubscriptionPlan.PLATINUM,
            PatientSubscriptionPlan.PREMIUM
    };

    public PatientSubscriptionPlanProvider(Faker faker) {
        super(faker);
    }

    public PatientSubscriptionPlan nextSubscriptionPlan() {
        return PLANS[getFaker().random().nextInt(PLANS.length)];
    }
}
