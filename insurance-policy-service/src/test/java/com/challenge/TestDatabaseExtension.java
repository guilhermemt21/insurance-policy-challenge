package com.challenge;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.extension.*;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

public class TestDatabaseExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private HikariDataSource dataSource;
    private Jdbi jdbi;

    @Override
    public void beforeAll(ExtensionContext context) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config_test.yml")) {
            if (in == null) {
                throw new RuntimeException("Arquivo config_test.yml não encontrado no classpath");
            }

            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(in);
            Map<String, Object> dbConfig = (Map<String, Object>) root.get("insurancePolicyDatabase");

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName((String) dbConfig.get("driverClass"));
            hikariConfig.setJdbcUrl(resolveEnvVarOrDefault((String) dbConfig.get("url")));
            hikariConfig.setUsername(resolveEnvVarOrDefault((String) dbConfig.get("user")));
            hikariConfig.setPassword(resolveEnvVarOrDefault((String) dbConfig.get("password")));
            hikariConfig.setMaximumPoolSize(10);

            this.dataSource = new HikariDataSource(hikariConfig);

            try (Connection conn = dataSource.getConnection()) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(conn));

                Liquibase liquibase = new Liquibase(
                        "migrations/migration_v1_001.sql",
                        new ClassLoaderResourceAccessor(),
                        database);

                liquibase.update("");
            }

            this.jdbi = Jdbi.create(dataSource);
            this.jdbi.installPlugin(new SqlObjectPlugin());

        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar banco de teste", e);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pc.getParameter().getType() == DatabaseContext.class;
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return new DatabaseContext(jdbi);
    }

    public static class DatabaseContext {
        private final Jdbi jdbi;

        public DatabaseContext(Jdbi jdbi) {
            this.jdbi = jdbi;
        }

        public Jdbi getJdbi() {
            return jdbi;
        }
    }

    private String resolveEnvVarOrDefault(String value) {
        if (value == null) return null;
        if (!value.startsWith("${") || !value.endsWith("}")) return value;

        String inner = value.substring(2, value.length() - 1); // tira ${ e }
        String[] parts = inner.split(":-");
        String envVar = parts[0];
        String defaultValue = parts.length > 1 ? parts[1] : null;
        String envValue = System.getenv(envVar);

        return envValue != null ? envValue : defaultValue;
    }

}
