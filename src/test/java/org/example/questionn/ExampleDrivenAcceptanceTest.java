package org.example.questionn;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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

public class ExampleDrivenAcceptanceTest
{
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @TempDir
    File databaseRoot;

    private ExampleDrivenMain.Example example;

    @BeforeEach
    public void startH2() throws Exception
    {
        final String databaseRootPath = databaseRoot.getAbsolutePath();
        example = ExampleDrivenMain.startExample(databaseRootPath, "shopp");
    }

    @AfterEach
    void stopH2() throws Exception
    {
        if (this.example != null)
        {
            this.example.stop();
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
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute())
        {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
