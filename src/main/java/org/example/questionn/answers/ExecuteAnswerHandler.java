package org.example.questionn.answers;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class ExecuteAnswerHandler implements Handler
{
    @Override
    public void handle(Context ctx)
    {
        final AnswerService answerService = ctx.get(AnswerService.class);
        answerService
            .executeAnswer(ctx.getPathBinding().getTokens().get("answer"))
            .map(Jackson::json)
            .then(ctx::render);
    }
}
