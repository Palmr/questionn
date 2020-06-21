package org.example.questionn.answers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.example.questionn.JdbiSource;
import org.example.questionn.http.NotFound;
import org.example.questionn.queries.QueryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;


import ratpack.registry.RegistrySpec;
import ratpack.test.handling.HandlingResult;
import ratpack.test.handling.RequestFixture;

class ExecuteAnswerHandlerTest
{
    private final RequestFixture requestFixture = RequestFixture.requestFixture();
    private final RegistrySpec registry = requestFixture.getRegistry();
    private final Map<String, Answer> answers = new HashMap<>();

    @BeforeEach
    void setUp()
    {
        final QueryService queryService = new QueryService(Collections.emptyMap());
        final JdbiSource jdbiSource = dataSourceName -> null;
        registry.add(
                AnswerService.class,
                new AnswerService(answers, queryService, jdbiSource));
    }

    @Test
    void missingAnswerTriggersNotFound()
    {
        requestFixture
                .pathBinding(Collections.singletonMap("answer", "lolz"))
                .body("{}", "application/json");

        HandlingResult result = requestFixture.handle(new ExecuteAnswerHandler());

        assertThat(
                result.exception(NotFound.class).getMessage(),
                Matchers.equalTo("Answer not found: lolz"));
    }

    @Test
    void missingQueryTriggersNotFound()
    {
        Answer a = new Answer();
        a.queryName = "query?";
        answers.put("lolz", a);

        requestFixture
                .pathBinding(Collections.singletonMap("answer", "lolz"))
                .body("{}", "application/json");

        HandlingResult result = requestFixture.handle(new ExecuteAnswerHandler());

        assertThat(
                result.exception(NotFound.class).getMessage(),
                Matchers.equalTo("Query not found: query?"));
    }
}