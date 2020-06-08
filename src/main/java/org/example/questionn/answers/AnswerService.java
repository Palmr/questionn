package org.example.questionn.answers;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;


import ratpack.exec.Promise;
import ratpack.exec.Result;
import ratpack.exec.internal.DefaultPromise;
import ratpack.registry.RegistrySpec;

public class AnswerService
{
    private final Map<String, Answer> answers = new HashMap<>();

    public void load(final Path baseDir, final Yaml yaml) throws IOException
    {
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(baseDir.resolve("data/answers")))
        {
            paths.forEach(answerFile -> {
                try (final FileInputStream input = new FileInputStream(answerFile.toFile()))
                {
                    final Answer answer = yaml.loadAs(input, Answer.class);
                    answers.put(answer.name, answer);
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

    public Promise<List<AnswerDetail>> getAllAnswers()
    {
        return new DefaultPromise<>(downstream -> {
            final List<AnswerDetail> allAnswers = answers.values().stream()
                    .map(AnswerDetail::new)
                    .collect(Collectors.toList());
            downstream.accept(Result.success(allAnswers));
        });
    }
}
