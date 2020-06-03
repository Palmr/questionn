package org.example.questionn.queries;

import java.util.Collections;
import java.util.List;

public final class Query
{
    public String name;
    public String queryText;
    public List<QueryParameter> queryParameters;

    public List<QueryParameter> getQueryParameters() {
        return queryParameters == null ? Collections.emptyList() : queryParameters;
    }

    @Override
    public String toString() {
        return "Query{" +
                "name='" + name + '\'' +
                ", queryText='" + queryText + '\'' +
                ", queryParameters=" + queryParameters +
                '}';
    }
}
