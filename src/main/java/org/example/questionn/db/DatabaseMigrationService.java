package org.example.questionn.db;

import org.flywaydb.core.Flyway;
import ratpack.service.Service;
import ratpack.service.StartEvent;

import javax.sql.DataSource;

public class DatabaseMigrationService implements Service {
    @Override
    public void onStart(final StartEvent event) {
        Flyway.configure()
                .dataSource(event.getRegistry().get(DataSource.class))
                .load()
                .migrate();
    }
}
