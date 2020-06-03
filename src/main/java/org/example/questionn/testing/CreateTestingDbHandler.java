package org.example.questionn.testing;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import static ratpack.jackson.Jackson.fromJson;

public class CreateTestingDbHandler implements Handler {

    @Override
    public void handle(Context ctx) {
        ctx.parse(fromJson(CreateTestingDbRequest.class))
                .then(createTokenRequest -> ctx.get(TestingDbService.class).createDbEntry(ctx, createTokenRequest)
                        .then(tokenId -> {
                            ctx.getResponse().status(Status.CREATED);
                            ctx.getResponse().getHeaders().add("Location", "/api/testing/db/" + tokenId);
                            ctx.getResponse().send();
                        }));
    }
}
