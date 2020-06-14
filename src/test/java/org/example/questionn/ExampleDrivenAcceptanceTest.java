package org.example.questionn;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Collections;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.example.questionn.db.DatabaseConfig;
import org.example.questionn.yaml.YamlLoader;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ratpack.server.RatpackServer;

public class ExampleDrivenAcceptanceTest
{
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @TempDir
    File databaseRoot;

    private Server server;
    private RatpackServer ratpackServer;

    @BeforeEach
    @SuppressFBWarnings(value = "DMI_EMPTY_DB_PASSWORD", justification = "It's a test. Cry me a river.")
    public void startH2() throws Exception
    {
        this.server = Server.createTcpServer(
                "-tcpPort", "8080",
                "-tcpAllowOthers",
                "-baseDir", databaseRoot.getAbsolutePath()).start();
        final String exampleDirectoryName = "shopp";

        final File exampleDirectory = new File("examples/" + exampleDirectoryName);
        final File ddl = new File(exampleDirectory, "bootstrap/ddl.sql");
        final File data = new File(exampleDirectory, "bootstrap/data.sql");

        String databaseUrl = "jdbc:h2:" + databaseRoot + ";MODE=mysql";
        try (final Connection conn = DriverManager.getConnection(databaseUrl, "sa", "");
             final Statement statement = conn.createStatement())
        {
            statement.execute("CREATE schema questionn");
            statement.execute("RUNSCRIPT FROM '" + ddl.getAbsolutePath() + "'");
            statement.execute("RUNSCRIPT FROM '" + data.getAbsolutePath() + "'");
        }

        ServerConfiguration serverConfiguration = new ServerConfiguration();

        DatabaseConfig questionnDatabase = new DatabaseConfig();
        questionnDatabase.url = databaseUrl + ";SCHEMA=questionn";
        questionnDatabase.username = "sa";
        questionnDatabase.password = "";

        DatabaseConfig shoppDatabase = new DatabaseConfig();
        shoppDatabase.url = databaseUrl + ";SCHEMA=" + exampleDirectoryName;
        shoppDatabase.username = "sa";
        shoppDatabase.password = "";

        RatpackConfiguration ratpackConfiguration = new RatpackConfiguration();
        ratpackConfiguration.isDevelopment = true;
        ratpackConfiguration.port = 8081;

        serverConfiguration.configurationPath = exampleDirectory.getAbsolutePath();
        serverConfiguration.questionnDatabase = questionnDatabase;
        serverConfiguration.databasesForQuery = Collections.singletonMap(exampleDirectoryName, shoppDatabase);
        serverConfiguration.ratpackConfiguration = ratpackConfiguration;

        ratpackServer = QuestionnMain.startRatpack(
                YamlLoader.newInstance(),
                serverConfiguration
        );
    }

    @AfterEach
    void stopH2() throws Exception
    {
        if (this.server != null)
        {
            this.server.stop();
        }

        if (this.ratpackServer != null)
        {
            this.ratpackServer.stop();
        }
    }

    @Test
    public void queryTotalSales() throws IOException
    {
        String result = post("http://localhost:8081/api/answers/sales_total", "");
        assertThat(result, containsString("12.72"));
    }

    @SuppressWarnings("SameParameterValue")
    String post(String url, String json) throws IOException
    {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute())
        {
            assert response.body() != null;
            return response.body().string();
        }
    }
}
