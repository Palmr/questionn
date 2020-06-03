package org.example.questionn.db;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;
import ratpack.guice.ConfigurableModule;

import javax.sql.DataSource;

public class SimpleConfiguredH2DataSourceModule extends ConfigurableModule<DatabaseConfig> {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public DataSource dataSource(DatabaseConfig config) {
        return JdbcConnectionPool.create(config.databaseUrl, config.username, config.password);
    }

    @Provides
    @Singleton
    public Jdbi jdbi(DatabaseConfig config) {
        return Jdbi.create(dataSource(config));
    }
}
