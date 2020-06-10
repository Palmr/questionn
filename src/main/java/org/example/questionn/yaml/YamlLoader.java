package org.example.questionn.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public final class YamlLoader
{
    private final Yaml yaml;

    private YamlLoader(Yaml yaml)
    {
        this.yaml = yaml;
    }

    public static YamlLoader newInstance()
    {
        return new YamlLoader(new Yaml());
    }

    public <T> T load(Path path, Class<T> type) throws IOException
    {
        try (final FileInputStream fis = new FileInputStream(path.toFile()))
        {
            return yaml.loadAs(fis, type);
        }
    }
}
