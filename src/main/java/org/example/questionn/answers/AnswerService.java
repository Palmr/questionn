package org.example.questionn.answers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.questionn.JdbiSource;
import org.example.questionn.http.NotFound;
import org.example.questionn.queries.QueryService;
import org.example.questionn.yaml.YamlLoader;


import ratpack.exec.Promise;
import ratpack.exec.Result;
import ratpack.exec.internal.DefaultPromise;
import ratpack.registry.RegistrySpec;

public final class AnswerService
{
    private final Map<String, Answer> answers;
    private final QueryService queryService;
    private final JdbiSource jdbiSource;

    AnswerService(
            final Map<String, Answer> answers,
            final QueryService queryService,
            final JdbiSource jdbiSource)
    {
        this.answers = answers;
        this.queryService = queryService;
        this.jdbiSource = jdbiSource;
    }

    public static AnswerService load(
            final Path baseDir,
            final YamlLoader yaml,
            final QueryService queryService,
            final JdbiSource jdbiSource)
            throws IOException
    {
        final Map<String, Answer> answers = new HashMap<>();
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(baseDir.resolve("answers")))
        {
            for (Path path : paths)
            {
                final Answer answer = yaml.load(path, Answer.class);
                answers.put(answer.name, answer);
            }
        }

        return new AnswerService(answers, queryService, jdbiSource);
    }

    public void registerEntries(final RegistrySpec registrySpec)
    {
        registrySpec.add(this)
                .add(new GetAllAnswersHandler())
                .add(new ExecuteAnswerHandler());
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

    public Promise<AnswerResult> executeAnswer(
            final String answerName,
            final Map<String, Object> parameters)
    {
        return lookupAnswer(answerName)
                .flatMap(answer -> queryService.runQuery(
                        answer.queryName,
                        parameters,
                        jdbiSource.jdbi(answer.dataSourceName)))
                .map(qr -> new AnswerResult(qr.metadataRow, qr.dataRows));
    }

    private Promise<Answer> lookupAnswer(String answerName)
    {
        return new DefaultPromise<>(downstream -> {
            Answer answer = answers.get(answerName);
            if (answer == null)
            {
                downstream.error(new NotFound("Answer not found: " + answerName));
            }
            else
            {
                downstream.success(answer);
            }
        });
    }
}
