package org.example.questionn;

import org.jdbi.v3.core.Jdbi;

public interface JdbiSource
{
    Jdbi jdbi(String dataSourceName);
}
