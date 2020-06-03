package org.example.questionn.answers;

import org.yaml.snakeyaml.Yaml;
import ratpack.exec.Promise;
import ratpack.exec.Result;
import ratpack.exec.internal.DefaultPromise;
import ratpack.registry.RegistrySpec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnswerService {
    private final Map<String, Answer> answers = new HashMap<>();

    public void load(final Path baseDir, final Yaml yaml) throws IOException {
        Files.newDirectoryStream(baseDir.resolve("data/answers")).forEach(answerFile -> {
            try {
                final Answer answer = yaml.loadAs(new FileInputStream(answerFile.toFile()), Answer.class);
                answers.put(answer.name, answer);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void registerEntries(final RegistrySpec registrySpec) {
        registrySpec.add(this)
                .add(new GetAllAnswersHandler());
    }

    public Promise<List<AnswerDetail>> getAllAnswers() {
        return new DefaultPromise<>(downstream -> {
            final List<AnswerDetail> allAnswers = answers.values().stream()
                    .map(AnswerDetail::new)
                    .collect(Collectors.toList());
            downstream.accept(Result.success(allAnswers));
        });
    }
}
