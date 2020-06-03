package org.example.questionn;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.questionn.answers.AnswerService;
import org.example.questionn.answers.GetAllAnswersHandler;
import org.example.questionn.db.DatabaseConfig;
import org.example.questionn.db.DatabaseMigrationService;
import org.example.questionn.db.SimpleConfiguredH2DataSourceModule;
import org.example.questionn.queries.QueryService;
import org.example.questionn.testing.CreateTestingDbHandler;
import org.example.questionn.testing.GetAllTestingDbHandler;
import org.example.questionn.testing.GetTestingDbHandler;
import org.example.questionn.testing.TestingDbService;
import org.yaml.snakeyaml.Yaml;
import ratpack.guice.Guice;
import ratpack.handling.RequestLogger;
import ratpack.http.MutableHeaders;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

import java.nio.file.Path;

public class QuestionnMain {
    public static void main(String... args) throws Exception {
        final Path baseDir = BaseDir.find();
        Yaml yaml = new Yaml();

        AnswerService answerService = new AnswerService();
        answerService.load(baseDir, yaml);

        QueryService queryService = new QueryService();
        queryService.load(baseDir, yaml);

        RatpackServer.start(server -> server
                .serverConfig(c -> c
                        .baseDir(baseDir)
                        .yaml("questionn.yml")
                        .require("/db", DatabaseConfig.class)
                )
                .registry(Guice.registry(b -> b
                        .module(SimpleConfiguredH2DataSourceModule.class)
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
//                                        .post("answers/:answer", ExecuteAnswerHandler.class)
//
//                                        .get("dashboards", GetAllDashboardsHandler.class)
//                                        .get("dashboards/:dashboard", GetDashboardHandler.class)
//                                        .post("dashboards/:dashboard", ExecuteDashboardHandler.class)
                                )

                                .files(f -> f.dir("web").indexFiles("index.html"))
                )
        );
    }
}
