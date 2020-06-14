package org.example.questionn.queries;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.example.questionn.answers.GetAllAnswersHandler;
import org.example.questionn.yaml.YamlLoader;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;


import ratpack.registry.RegistrySpec;

public final class QueryService
{
    private final Map<String, Query> queries;

    private QueryService(Map<String, Query> queries)
    {
        this.queries = queries;
    }

    public static QueryService load(final Path baseDir, final YamlLoader yaml) throws IOException
    {
        final Map<String, Query> queries = new HashMap<>();
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(baseDir.resolve("queries")))
        {
            for (Path path : paths)
            {
                final Query query = yaml.load(path, Query.class);
                queries.put(query.name, query);
            }
        }
        return new QueryService(queries);
    }

    public void registerEntries(final RegistrySpec registrySpec)
    {
        registrySpec.add(this)
                .add(new GetAllAnswersHandler());
    }

    public QueryResult runQuery(final String queryName, final Jdbi jdbi) throws Exception
    {
        Query query = queries.get(queryName);

        return jdbi.inTransaction(TransactionIsolationLevel.REPEATABLE_READ, (HandleCallback<QueryResult, Exception>)handle -> {
            ResultIterable<Double> jdbiQuery = handle.createQuery(query.queryText).mapTo(Double.class);

            return new QueryResult(jdbiQuery.findFirst().orElseThrow(() -> new RuntimeException("I just want my first test to pass :-(")));
        });
    }
}
