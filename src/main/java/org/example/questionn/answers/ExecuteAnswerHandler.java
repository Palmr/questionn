package org.example.questionn.answers;

import com.google.common.collect.Iterables;

import org.example.questionn.Csv;


import ratpack.func.Function;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;
import ratpack.jackson.JsonRender;

public class ExecuteAnswerHandler implements Handler
{
    @Override
    public void handle(Context ctx)
    {
        final AnswerService answerService = ctx.get(AnswerService.class);

        final String accept = ctx.getRequest().getHeaders().get("Accept");
        if (accept != null && accept.equals("text/csv"))
        {
            answerService
                    .executeAnswer(ctx.getPathBinding().getTokens().get("answer"))
                    .map(a -> new Csv()
                    {
                        @Override
                        public String[] fieldNames()
                        {
                            return a.metadataRow.fieldNames;
                        }

                        @Override
                        public Iterable<String[]> records()
                        {
                            return Iterables.transform(a.dataRows, d -> d.fields);
                        }
                    })
                    .then(ctx::render);
        }
        else
        {
            Function<AnswerResult, JsonRender> json = Jackson::json;

            answerService
                    .executeAnswer(ctx.getPathBinding().getTokens().get("answer"))
                    .map(json)
                    .then(ctx::render);
        }
    }
}
