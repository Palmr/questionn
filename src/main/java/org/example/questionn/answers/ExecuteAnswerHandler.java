package org.example.questionn.answers;


import java.util.Map;
import java.util.Optional;

import com.google.common.reflect.TypeToken;


import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class ExecuteAnswerHandler implements Handler
{
    public static final TypeToken<Map<String, Object>> PARAMETER_TYPE = new TypeToken<Map<String, Object>>() {};

    @Override
    public void handle(Context ctx)
    {
        final AnswerService answerService = ctx.get(AnswerService.class);
        final String accept = Optional
                .ofNullable(ctx.getRequest().getHeaders().get("Accept"))
                .orElse("application/json");
        final String answer = ctx.getPathBinding().getTokens().get("answer");
        final Promise<Map<String, Object>> parameters = ctx.parse(PARAMETER_TYPE);
        switch (accept)
        {
            case "text/csv":
                parameters
                        .map(p -> answerService.executeAnswer(answer, p))
                        .then(ctx::render);

                break;
            case "application/json":
                parameters
                        .flatMap(p -> answerService.executeAnswer(answer, p))
                        .map(Jackson::json)
                        .then(ctx::render);
                break;
            default:
                ctx.clientError(400);
        }
    }
}
