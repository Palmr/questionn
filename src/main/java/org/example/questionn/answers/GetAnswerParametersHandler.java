package org.example.questionn.answers;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class GetAnswerParametersHandler implements Handler
{
    @Override
    public void handle(Context ctx)
    {
        final String answerName = ctx.getPathBinding().getTokens().get("answer");

        ctx.get(AnswerService.class)
            .getAnswerParameters(answerName)
            .map(Jackson::json)
            .then(ctx::render);
    }
}
