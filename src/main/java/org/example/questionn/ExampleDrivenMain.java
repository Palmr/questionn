package org.example.questionn;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Collections;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.example.questionn.db.DatabaseConfig;
import org.example.questionn.yaml.YamlLoader;
import org.h2.tools.Server;


import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import ratpack.server.RatpackServer;

public class ExampleDrivenMain
{

    public static final int RATPACK_PORT = 5050;
    public static final String H2_DATABASE_PORT = "8082";

    public static void main(String... args) throws Exception
    {
        ArgumentParser parser = ArgumentParsers
            .newFor("ExampleDrivenMain")
            .build()
            .defaultHelp(true)
            .description("Run a Questionn server based on an example directory");
        parser.addArgument("-d", "--example-directory")
            .required(true)
            .help("The example directory name (e.g 'shopp')");
        try
        {
            final Namespace namespace = parser.parseArgs(args);
            final String exampleDirectory = namespace.getString("example_directory");

            Path path = Files.createTempDirectory("questionn-eg");
            Example example = startExample(path.toAbsolutePath().toString(), exampleDirectory);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try
                {
                    example.stop();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }));

        }
        catch (ArgumentParserException e)
        {
            parser.handleError(e);
            System.exit(1);
        }
    }

    static final class Example
    {
        private final Server server;
        private final RatpackServer ratpackServer;

        public Example(
                Server server,
                RatpackServer ratpackServer)
        {
            this.server = server;
            this.ratpackServer = ratpackServer;
        }

        public void stop() throws Exception
        {
            this.server.stop();
            this.ratpackServer.stop();
        }
    }

    @SuppressFBWarnings(
            value = { "DMI_EMPTY_DB_PASSWORD", "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE" },
            justification = "This is only used to run examples")
    static Example startExample(
            final String databaseRootPath,
            final String exampleDirectoryName) throws Exception
    {
        final Server server = Server.createTcpServer(
                "-tcpPort", H2_DATABASE_PORT,
                "-tcpAllowOthers",
                "-baseDir", databaseRootPath).start();

        final File exampleDirectory = new File("examples/" + exampleDirectoryName);
        final File ddl = new File(exampleDirectory, "bootstrap/ddl.sql");
        final File data = new File(exampleDirectory, "bootstrap/data.sql");

        String databaseUrl = "jdbc:h2:" + databaseRootPath + ";MODE=mysql";
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
        ratpackConfiguration.port = RATPACK_PORT;

        serverConfiguration.configurationPath = exampleDirectory.getAbsolutePath();
        serverConfiguration.questionnDatabase = questionnDatabase;
        serverConfiguration.databasesForQuery = Collections.singletonMap(exampleDirectoryName, shoppDatabase);
        serverConfiguration.ratpackConfiguration = ratpackConfiguration;

        final RatpackServer ratpackServer = QuestionnMain.startRatpack(
                YamlLoader.newInstance(),
                serverConfiguration
        );

        return new Example(server, ratpackServer);
    }
}
