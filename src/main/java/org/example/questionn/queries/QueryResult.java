package org.example.questionn.queries;

import java.util.List;

public class QueryResult
{
    public final QueryService.MetadataRow metadataRow;
    public final List<QueryService.DataRow> dataRows;

    public QueryResult(
            QueryService.MetadataRow metadataRow,
            List<QueryService.DataRow> dataRows)
    {

        this.metadataRow = metadataRow;
        this.dataRows = dataRows;
    }
}
