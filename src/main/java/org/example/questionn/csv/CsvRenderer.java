package org.example.questionn.csv;

import ratpack.handling.Context;
import ratpack.render.RendererSupport;

public class CsvRenderer extends RendererSupport<Csv>
{
    @Override
    public void render(final Context ctx, final Csv csv)
    {
        ctx.getResponse().send("text/csv", csv.toCsv());
    }
}
