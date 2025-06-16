package com.challenge;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.core.setup.Environment;
import org.jdbi.v3.core.Jdbi;

public class AppBase {
    private final Injector injector;

    public AppBase(InsurancePolicyServiceConfiguration config, Environment env) {
        final Jdbi jdbiInstance = JdbiFactory.newInstance(env, config.getDataSourceFactory(), "mysql");

        this.injector = Guice.createInjector(
                new InsurancePolicyServiceModule(jdbiInstance, config)
        );

    }

    public Injector getInjector() {
        return injector;
    }
}
