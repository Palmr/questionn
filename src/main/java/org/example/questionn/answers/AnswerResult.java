package org.example.questionn.answers;

import com.google.common.collect.Iterables;

import org.example.questionn.csv.Csv;
import org.example.questionn.queries.QueryService;

import java.util.List;

public class AnswerResult implements Csv
{
    public final QueryService.MetadataRow metadataRow;
    public final List<QueryService.DataRow> dataRows;

    public AnswerResult(
            QueryService.MetadataRow metadataRow,
            List<QueryService.DataRow> dataRows)
    {
        this.metadataRow = metadataRow;
        this.dataRows = dataRows;
    }

    @Override
    public String[] fieldNames()
    {
        return metadataRow.fieldNames;
    }

    @Override
    public Iterable<String[]> records()
    {
        return Iterables.transform(dataRows, d -> d.fields);
    }
}
