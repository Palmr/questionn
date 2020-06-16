package org.example.questionn.answers;

import org.example.questionn.queries.QueryService;

import java.util.List;

public class AnswerResult
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
}
