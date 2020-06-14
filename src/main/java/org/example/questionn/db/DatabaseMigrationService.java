package org.example.questionn.db;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;


import ratpack.service.Service;
import ratpack.service.StartEvent;

public class DatabaseMigrationService implements Service
{
    @Override
    public void onStart(final StartEvent event)
    {
        DataSource dataSource = event.getRegistry().get(DataSource.class);
        Flyway.configure()
                .dataSource(dataSource)
                .schemas("questionn")
                .load()
                .migrate();
    }
}
