package org.example.questionn.testing;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class GetTestingDbHandler implements Handler
{
    @Override
    public void handle(Context ctx)
    {
        final long entryId = Long.parseLong(ctx.getPathTokens().get("entryId"));

        ctx.get(TestingDbService.class).getDbEntry(ctx, entryId)
                .map(Jackson::json)
                .then(ctx::render);
    }
}
