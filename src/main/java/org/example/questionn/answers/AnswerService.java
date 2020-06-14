package org.example.questionn.answers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.questionn.JdbiService;
import org.example.questionn.queries.QueryResult;
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
    private final JdbiService jdbiService;

    private AnswerService(
            final Map<String, Answer> answers,
            final QueryService queryService,
            final JdbiService jdbiService)
    {
        this.answers = answers;
        this.queryService = queryService;
        this.jdbiService = jdbiService;
    }

    public static AnswerService load(
            final Path baseDir,
            final YamlLoader yaml,
            final QueryService queryService,
            final JdbiService jdbiService)
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

        return new AnswerService(answers, queryService, jdbiService);
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

    public Promise<AnswerResult> executeAnswer(final String answerName)
    {
        return new DefaultPromise<>(downstream -> {
            final Answer answer = this.answers.get(answerName);

            QueryResult r = queryService.runQuery(answer.queryName, jdbiService.jdbi(answer.dataSourceName));
            AnswerResult ar = new AnswerResult(r.num);

            downstream.accept(Result.success(ar));
        });
    }
}
