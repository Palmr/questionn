package org.example.questionn.queries;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.example.questionn.yaml.YamlLoader;
import org.example.questionn.answers.GetAllAnswersHandler;


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
}
