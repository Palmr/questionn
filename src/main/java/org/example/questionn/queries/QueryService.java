package org.example.questionn.queries;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.questionn.answers.GetAllAnswersHandler;
import org.example.questionn.http.NotFound;
import org.example.questionn.yaml.YamlLoader;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;


import ratpack.exec.Promise;
import ratpack.exec.Result;
import ratpack.exec.internal.DefaultPromise;
import ratpack.registry.RegistrySpec;

public final class QueryService
{
    private final Map<String, Query> queries;

    public QueryService(Map<String, Query> queries)
    {
        this.queries = queries;
    }

    public static QueryService load(final Path baseDir, final YamlLoader yaml) throws IOException
    {
        final Map<String, Query> queries = new HashMap<>();
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(baseDir.resolve("queries")))
        {
            for (Path path : paths)
            {
                final Query query = yaml.load(path, Query.class);
                queries.put(query.name, query);
            }
        }
        return new QueryService(queries);
    }

    public void registerEntries(final RegistrySpec registrySpec)
    {
        registrySpec.add(this)
                .add(new GetAllAnswersHandler());
    }

    public Promise<QueryResult> runQuery(
            final String queryName,
            final Map<String, Object> parameters,
            final Jdbi jdbi)
    {
        return new DefaultPromise<>(downstream -> {
            Query query = queries.get(queryName);
            if (query == null)
            {
                downstream.error(new NotFound("Query not found: " + queryName));
            }
            else
            {
                downstream.accept(Result.success(jdbi.inTransaction(TransactionIsolationLevel.REPEATABLE_READ, (HandleCallback<QueryResult, Exception>)handle -> {
                    org.jdbi.v3.core.statement.Query q =
                            handle.createQuery(query.queryText);
                    parameters.forEach(q::bind);

                    return q.execute((statementSupplier, ctx) -> {
                        ResultSet resultSet = statementSupplier.get().executeQuery();

                        return new QueryResult(metadataRow(resultSet.getMetaData()), dataRows(resultSet));
                    });
                })));
            }
        });
    }

    private List<DataRow> dataRows(ResultSet resultSet) throws SQLException
    {
        final List<DataRow> rows = new ArrayList<>();

        resultSet.beforeFirst();
        while (resultSet.next())
        {
            rows.add(dataRow(resultSet));
        }

        return rows;
    }

    private DataRow dataRow(ResultSet resultSet) throws SQLException
    {
        int columnCount = resultSet.getMetaData().getColumnCount();
        final String[] values = new String[columnCount];

        for (int i = 0; i < columnCount; i++)
        {
            values[i]  = resultSet.getString(i + 1);
        }

        return new DataRow(values);
    }

    private MetadataRow metadataRow(ResultSetMetaData metaData) throws SQLException
    {
        final int columnCount = metaData.getColumnCount();
        final String[] columnNames = new String[columnCount];
        final String[] columnTypes = new String[columnCount];
        for (int i = 0; i < columnCount; i++)
        {
            columnNames[i] = metaData.getColumnName(i + 1);
            columnTypes[i] = metaData.getColumnTypeName(i + 1);
        }

        return new MetadataRow(columnNames, columnTypes);
    }

    public interface DatabaseRow
    {

    }

    public static final class MetadataRow implements DatabaseRow
    {
        public final String[] fieldNames;
        public final String[] fieldTypes;

        public MetadataRow(final String[] fieldNames, final String[] fieldTypes)
        {
            this.fieldNames = fieldNames;
            this.fieldTypes = fieldTypes;
        }
    }

    public static final class DataRow implements DatabaseRow
    {
        public final String[] fields;

        public DataRow(final String[] fields)
        {
            this.fields = fields;
        }
    }
}
