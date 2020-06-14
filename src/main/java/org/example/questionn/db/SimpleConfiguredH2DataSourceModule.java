package org.example.questionn.db;

import javax.sql.DataSource;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;


import ratpack.guice.ConfigurableModule;

public class SimpleConfiguredH2DataSourceModule extends ConfigurableModule<DatabaseConfig>
{
    private final DatabaseConfig questionnDatabase;

    public SimpleConfiguredH2DataSourceModule(DatabaseConfig databaseConfig)
    {
        this.questionnDatabase = databaseConfig;
    }

    @Override
    protected void configure()
    {
    }

    @Provides
    @Singleton
    public DataSource dataSource()
    {
        return JdbcConnectionPool.create(questionnDatabase.url, questionnDatabase.username, questionnDatabase.password);
    }

    @Provides
    @Singleton
    public Jdbi jdbi()
    {
        return Jdbi.create(dataSource());
    }
}
