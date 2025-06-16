package com.challenge;


import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.SerializableTransactionRunner;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.sql.DataSource;

public class JdbiFactory {

    public static Jdbi newInstance(Environment environment, DataSourceFactory dataSourceFactory, String name) {
        final DataSource dataSource = dataSourceFactory.build(environment.metrics(), name);
        final Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.setTransactionHandler(new SerializableTransactionRunner());

        return jdbi;
    }
}