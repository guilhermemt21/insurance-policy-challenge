package com.challenge;

import com.challenge.api.FraudAnalysisClient;
import com.challenge.core.paymentService.PaymentService;
import com.challenge.core.policy.PolicyService;
import com.challenge.core.user.UserService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

public class InsurancePolicyServiceModule extends AbstractModule {
    private final Jdbi jdbi;
    private final InsurancePolicyServiceConfiguration config;

    public InsurancePolicyServiceModule(Jdbi jdbi, InsurancePolicyServiceConfiguration config) {
        this.jdbi = jdbi;
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(Jdbi.class).toInstance(jdbi);
        bind(PolicyService.class);
        bind(UserService.class);
        bind(PaymentService.class);
        bind(InsurancePolicyServiceConfiguration.class).toInstance(config);
    }
}
