package org.example.questionn.answers;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class GetAllAnswersHandler implements Handler {
    @Override
    public void handle(Context ctx) {
        ctx.get(AnswerService.class).getAllAnswers()
                .map(Jackson::json)
                .then(ctx::render);
    }
}
