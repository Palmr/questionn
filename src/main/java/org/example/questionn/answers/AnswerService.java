package org.example.questionn.answers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.questionn.yaml.YamlLoader;


import ratpack.exec.Promise;
import ratpack.exec.Result;
import ratpack.exec.internal.DefaultPromise;
import ratpack.registry.RegistrySpec;

public final class AnswerService
{
    private final Map<String, Answer> answers;

    private AnswerService(Map<String, Answer> answers) {

        this.answers = answers;
    }

    public static AnswerService load(
        final Path baseDir,
        final YamlLoader yaml) throws IOException
    {
        final Map<String, Answer> answers = new HashMap<>();
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(baseDir.resolve("data/answers"))) {
            for (Path path : paths) {
                final Answer answer = yaml.load(path, Answer.class);
                answers.put(answer.name, answer);
            }
        }

        return new AnswerService(answers);
    }

    public void registerEntries(final RegistrySpec registrySpec)
    {
        registrySpec.add(this)
                .add(new GetAllAnswersHandler());
    }

    Promise<List<AnswerDetail>> getAllAnswers()
    {
        return new DefaultPromise<>(downstream -> {
            final List<AnswerDetail> allAnswers = answers.values().stream()
                    .map(AnswerDetail::new)
                    .collect(Collectors.toList());
            downstream.accept(Result.success(allAnswers));
        });
    }
}
