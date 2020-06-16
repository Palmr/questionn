package org.example.questionn;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import io.netty.buffer.ByteBuf;


import io.netty.buffer.PooledByteBufAllocator;


import org.example.questionn.answers.AnswerService;
import org.example.questionn.answers.ExecuteAnswerHandler;
import org.example.questionn.answers.GetAllAnswersHandler;
import org.example.questionn.db.DatabaseMigrationService;
import org.example.questionn.db.SimpleConfiguredH2DataSourceModule;
import org.example.questionn.queries.QueryService;
import org.example.questionn.testing.CreateTestingDbHandler;
import org.example.questionn.testing.GetAllTestingDbHandler;
import org.example.questionn.testing.GetTestingDbHandler;
import org.example.questionn.testing.TestingDbService;
import org.example.questionn.yaml.YamlLoader;


import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


import ratpack.guice.Guice;
import ratpack.handling.Context;
import ratpack.handling.RequestLogger;
import ratpack.http.MutableHeaders;
import ratpack.http.Response;
import ratpack.render.RendererSupport;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;
import ratpack.stream.Streams;
import ratpack.stream.TransformablePublisher;

public class QuestionnMain
{
    public static void main(String... args) throws Exception
    {
        YamlLoader yamlLoader = YamlLoader.newInstance();
        ArgumentParser parser = ArgumentParsers
            .newFor("QuestionnMain")
            .build()
            .defaultHelp(true)
            .description("Run a Questionn server");
        parser.addArgument("-c", "--server-config-file")
            .required(true)
            .help("Path to the questionn server's config yaml");
        try
        {
            final Namespace namespace = parser.parseArgs(args);
            final String serverConfigPath = namespace.getString("server_config_file");
            final Path path = Paths.get(serverConfigPath);

            final ServerConfiguration serverConfiguration = yamlLoader.load(path, ServerConfiguration.class);
            startRatpack(yamlLoader, serverConfiguration);
        }
        catch (ArgumentParserException e)
        {
            parser.handleError(e);
            System.exit(1);
        }
    }

    static RatpackServer startRatpack(
        YamlLoader yamlLoader,
        ServerConfiguration serverConfiguration) throws Exception
    {
        final Path baseDir = Paths.get(serverConfiguration.configurationPath);
        final QueryService queryService = QueryService.load(baseDir, yamlLoader);
        final JdbiService jdbiService = new JdbiService(serverConfiguration.databasesForQuery);
        final AnswerService answerService = AnswerService.load(baseDir, yamlLoader, queryService, jdbiService);

        return RatpackServer.start(server -> server
                .serverConfig(c -> c
                    .baseDir(BaseDir.find())
                    .port(serverConfiguration.ratpackConfiguration.port)
                    .development(serverConfiguration.ratpackConfiguration.isDevelopment)
                )
                .registry(Guice.registry(b -> b
                    .module(new SimpleConfiguredH2DataSourceModule(serverConfiguration.questionnDatabase))
                    .add(new CsvRenderer())
                    .add(new DatabaseMigrationService())
                    .add(new ObjectMapper()
                        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                        .registerModule(new Jdk8Module())
                        .registerModule(new JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    )
                    .with(answerService::registerEntries)
                    .with(queryService::registerEntries)
                    .with(TestingDbService::registerEntries)
                ))
                .handlers(chain ->
                        chain
                            .all(RequestLogger.ncsa())

                            .all(ctx -> {
                                MutableHeaders headers = ctx.getResponse().getHeaders();
                                headers.add("Access-Control-Allow-Origin", "*");
                                headers.add("Access-Control-Allow-Methods", "GET,PATCH,POST,DELETE,OPTIONS");
                                headers.add("Access-Control-Allow-Headers", "Content-Type,X-Auth-Token");
                                ctx.next();
                            })

                            .prefix("api", tokenChain -> tokenChain
                                    .get("testing/db", GetAllTestingDbHandler.class)
                                    .post("testing/db/add", CreateTestingDbHandler.class)
                                    .get("testing/db/:entryId", GetTestingDbHandler.class)

                                    .get("answers", GetAllAnswersHandler.class)
//                                        .get("answers/:answer", GetAnswerHandler.class)
                                    .post("answers/:answer", ExecuteAnswerHandler.class)
//
//                                        .get("dashboards", GetAllDashboardsHandler.class)
//                                        .get("dashboards/:dashboard", GetDashboardHandler.class)
//                                        .post("dashboards/:dashboard", ExecuteDashboardHandler.class)
                            )

                            .files(f -> f.dir("web").indexFiles("index.html"))
                )
        );
    }

    public static class CsvRenderer extends RendererSupport<Csv>
    {
        @Override
        public void render(final Context ctx, final Csv csv)
        {
            Response response = ctx.getResponse();
            response.getHeaders().add("Content-Type", "text/csv");
            TransformablePublisher<ByteBuf> values = Streams.publish(csv.records())
                    .map(r -> {
                        final ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
                        final String str = "\n" + String.join(",", r);
                        byteBuf.writeBytes(str.getBytes(StandardCharsets.UTF_8));

                        return byteBuf;
                    });
            String join = String.join(",", csv.fieldNames());
            final ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
            byteBuf.writeBytes(join.getBytes(StandardCharsets.UTF_8));
            TransformablePublisher<ByteBuf> header = Streams.publish(Collections.singletonList(byteBuf));

            response.sendStream(Streams.concat(Arrays.asList(header, values)));
        }
    }
}
