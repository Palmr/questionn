package org.example.questionn.testing;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class GetAllTestingDbHandler implements Handler {
    @Override
    public void handle(Context ctx) {
        ctx.get(TestingDbService.class).getAllDbEntries(ctx)
                .map(Jackson::json)
                .then(ctx::render);
    }
}
