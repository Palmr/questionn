package org.example.questionn.csv;

import ratpack.handling.Context;
import ratpack.render.RendererSupport;

import static ratpack.http.ResponseChunks.stringChunks;

public class CsvRenderer extends RendererSupport<Csv>
{
    @Override
    public void render(final Context ctx, final Csv csv)
    {
        ctx.render(stringChunks("text/csv", csv.toStreamingCsv()));
    }
}
