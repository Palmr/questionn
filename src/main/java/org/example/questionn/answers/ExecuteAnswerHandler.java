package org.example.questionn.answers;


import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;


import java.util.Optional;

public class ExecuteAnswerHandler implements Handler
{
    @Override
    public void handle(Context ctx)
    {
        final AnswerService answerService = ctx.get(AnswerService.class);

        final String accept = Optional
                .ofNullable(ctx.getRequest().getHeaders().get("Accept"))
                .orElse("application/json");
        switch (accept)
        {
            case "text/csv":
                answerService
                        .executeAnswer(ctx.getPathBinding().getTokens().get("answer"))
                        .then(ctx::render);
                break;
            case "application/json":
                answerService
                        .executeAnswer(ctx.getPathBinding().getTokens().get("answer"))
                        .map(Jackson::json)
                        .then(ctx::render);
                break;
            default:
                ctx.clientError(400);
        }
    }
}
