package com.challenge;

import com.challenge.core.policy.PolicyResource;
import com.challenge.exception.IllegalStateExceptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Injector;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.migrations.MigrationsBundle;


public class InsurancePolicyServiceApplication extends Application<InsurancePolicyServiceConfiguration> {

    public static Injector injector;

    public static void main(String[] args) throws Exception {
        new InsurancePolicyServiceApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<InsurancePolicyServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(InsurancePolicyServiceConfiguration insurancePolicyServiceConfiguration) {
                return insurancePolicyServiceConfiguration.getDataSourceFactory();
            }
        });
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(true))
        );
    }

    @Override
    public void run(InsurancePolicyServiceConfiguration configuration, Environment environment) {
        AppBase appBase = new AppBase(configuration, environment);
        injector = appBase.getInjector();

        JerseyEnvironment jersey = environment.jersey();
        jersey.register(injector.getInstance(PolicyResource.class));
        jersey.register(injector.getInstance(IllegalStateExceptionMapper.class));
        ObjectMapper mapper = environment.getObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }


}
