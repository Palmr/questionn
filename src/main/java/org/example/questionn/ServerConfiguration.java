package org.example.questionn;

import org.example.questionn.db.DatabaseConfig;

import java.util.Map;

public final class ServerConfiguration
{
    public RatpackConfiguration ratpackConfiguration;
    // Questionn might want to store its own stuff in a DB
    public DatabaseConfig questionnDatabase;
    // The root of the queries/answers tree
    public String configurationPath;
    // ...and the databases they can read from
    public Map<String, DatabaseConfig> databasesForQuery;
}
