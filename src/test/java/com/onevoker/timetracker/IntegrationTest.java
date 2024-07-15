package com.onevoker.timetracker;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.SearchPathResourceAccessor;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.sql.SQLException;

@Testcontainers
public abstract class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;

    private static final String PSQL_IMAGE = "postgres:16";
    private static final String DB_NAME = "timetracker";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String MIGRATIONS_DIRECTORY = "migrations";
    private static final String CHANGE_LOG_FILE = "master.xml";
    private static final String JDBC_PROPERTY_URL = "spring.datasource.url";
    private static final String JDBC_PROPERTY_USERNAME = "spring.datasource.username";
    private static final String JDBC_PROPERTY_PASSWORD = "spring.datasource.password";

    static {
        POSTGRES = new PostgreSQLContainer<>(PSQL_IMAGE)
                .withDatabaseName(DB_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);
        POSTGRES.start();

        runMigrations(POSTGRES);
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setSuppressClose(true);
        dataSource.setUrl(c.getJdbcUrl());
        dataSource.setUsername(c.getUsername());
        dataSource.setPassword(c.getPassword());

        String path = new File(".").toPath()
                .toAbsolutePath()
                .getParent()
                .resolve(MIGRATIONS_DIRECTORY)
                .toString();

        try {
            Liquibase liquibase = new Liquibase(
                    CHANGE_LOG_FILE,
                    new SearchPathResourceAccessor(path),
                    new JdbcConnection(dataSource.getConnection())
            );

            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    private static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add(JDBC_PROPERTY_URL, POSTGRES::getJdbcUrl);
        registry.add(JDBC_PROPERTY_USERNAME, POSTGRES::getUsername);
        registry.add(JDBC_PROPERTY_PASSWORD, POSTGRES::getPassword);
    }
}
