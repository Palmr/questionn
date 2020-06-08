package org.example.questionn.queries;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.example.questionn.answers.GetAllAnswersHandler;
import org.yaml.snakeyaml.Yaml;


import ratpack.registry.RegistrySpec;

public class QueryService
{
    private final Map<String, Query> queries = new HashMap<>();

    public void load(final Path baseDir, final Yaml yaml) throws IOException
    {
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(baseDir.resolve("data/queries")))
        {
            paths.forEach(answerFile -> {
                try (final FileInputStream input = new FileInputStream(answerFile.toFile()))
                {
                    final Query query = yaml.loadAs(input, Query.class);
                    queries.put(query.name, query);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }

    public void registerEntries(final RegistrySpec registrySpec)
    {
        registrySpec.add(this)
                .add(new GetAllAnswersHandler());
    }
}
