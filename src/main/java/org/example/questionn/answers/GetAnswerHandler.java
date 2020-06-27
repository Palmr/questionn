package org.example.questionn.answers;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class GetAnswerHandler implements Handler
{
    @Override
    public void handle(Context ctx)
    {
        final String answerName = ctx.getPathBinding().getTokens().get("answer");

        ctx.get(AnswerService.class)
            .getAnswer(answerName)
            .map(Jackson::json)
            .then(ctx::render);
    }
}
