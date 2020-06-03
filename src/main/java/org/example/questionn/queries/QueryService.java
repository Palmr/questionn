package org.example.questionn.queries;

import org.example.questionn.answers.GetAllAnswersHandler;
import org.yaml.snakeyaml.Yaml;
import ratpack.registry.RegistrySpec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QueryService {
    private final Map<String, Query> queries = new HashMap<>();

    public void load(final Path baseDir, final Yaml yaml) throws IOException {
        Files.newDirectoryStream(baseDir.resolve("data/queries")).forEach(answerFile -> {
            try {
                final Query query = yaml.loadAs(new FileInputStream(answerFile.toFile()), Query.class);
                queries.put(query.name, query);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void registerEntries(final RegistrySpec registrySpec) {
        registrySpec.add(this)
                .add(new GetAllAnswersHandler());
    }
}
