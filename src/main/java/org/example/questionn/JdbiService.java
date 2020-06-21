package org.example.questionn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.questionn.db.DatabaseConfig;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;


import ratpack.service.Service;

public class JdbiService implements Service, JdbiSource
{
    private final Map<String, DatabaseConfig> databasesForQuery;
    private final Map<String, Jdbi> jdbis = new ConcurrentHashMap<>();

    public JdbiService(Map<String, DatabaseConfig> databasesForQuery)
    {
        this.databasesForQuery = databasesForQuery;
    }

    @Override
    public Jdbi jdbi(String dataSourceName)
    {
        return jdbis.computeIfAbsent(dataSourceName, k -> {
            final DatabaseConfig databaseConfig = databasesForQuery.get(dataSourceName);
            return Jdbi.create(JdbcConnectionPool.create(databaseConfig.url, databaseConfig.username, databaseConfig.password));
        });
    }
}
