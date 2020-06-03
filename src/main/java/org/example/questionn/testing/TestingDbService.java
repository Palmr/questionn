package org.example.questionn.testing;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.registry.RegistrySpec;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TestingDbService {

    public static void registerEntries(final RegistrySpec registrySpec) {
        registrySpec.add(new TestingDbService())
                .add(new GetAllTestingDbHandler())
                .add(new GetTestingDbHandler())
                .add(new CreateTestingDbHandler());
    }

    public Promise<List<DbEntry>> getAllDbEntries(final Context ctx) {
        return Blocking.get(() -> ctx.get(Jdbi.class).withHandle(handle ->
                handle.createQuery("SELECT * FROM test_table")
                        .map(DbEntryRowMapper.INSTANCE)
                        .list()));
    }

    public Promise<Long> createDbEntry(final Context ctx, final CreateTestingDbRequest request) {
        return Blocking.get(() -> ctx.get(Jdbi.class).withHandle(handle ->
                handle.createUpdate("INSERT INTO test_table (description) VALUES (:description)")
                        .bind("description", request.description)
                        .executeAndReturnGeneratedKeys()
                        .map(c -> c.getColumn("id", Long.class))
                        .one()
        ));
    }

    public Promise<DbEntry> getDbEntry(final Context ctx, final Long entryId) {
        return Blocking.get(() -> ctx.get(Jdbi.class).withHandle(handle ->
                handle.createQuery("SELECT * FROM test_table where id = :entryId")
                        .bind("entryId", entryId)
                        .map(DbEntryRowMapper.INSTANCE)
                        .one()));
    }

    private final static class DbEntryRowMapper implements RowMapper<DbEntry> {
        private static final DbEntryRowMapper INSTANCE = new DbEntryRowMapper();

        private DbEntryRowMapper() {
        }

        @Override
        public DbEntry map(final ResultSet rs, final StatementContext ctx) throws SQLException {
            return new DbEntry(
                    rs.getLong("id"),
                    rs.getString("description"),
                    rs.getTimestamp("created_datetime").toLocalDateTime()
            );
        }
    }
}
