package org.example.questionn;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

    static @TempDir File databaseRoot;

    private static ExampleDrivenMain.Example example;

    @BeforeAll
    public static void startH2() throws Exception
    {
        final String databaseRootPath = databaseRoot.getAbsolutePath();
        example = ExampleDrivenMain.startExample(databaseRootPath, "shopp");
    }

    @AfterAll
    public static void stopH2() throws Exception
    {
        if (example != null)
        {
            example.stop();
        }
    }

    @Test
    public void queryTotalSales() throws IOException
    {
        String result = post("http://localhost:8081/api/answers/sales_total");
        assertThat(result, containsString("12.72"));
    }

    @Test
    public void queryTotalSalesByCustomer() throws IOException
    {
        String result = post("http://localhost:8081/api/answers/sales_total_by_customer");
        assertThat(result, containsString("\"Alice Brown\",\"5.34\""));
    }

    @Test
    public void queryTotalSalesByCustomerAsCsv() throws IOException
    {
        String result = post("http://localhost:8081/api/answers/sales_total_by_customer",
                r -> r.header("Accept", "text/csv"));
        assertThat(result, containsString(
                "CUSTOMER_NAME,SALES_TOTAL\n" +
                "Alice Brown,5.34\n" +
                "Chris Dickinson,5.88\n" +
                "Gareth Hughes,1.50"
        ));
    }

    String post(String url) throws IOException
    {
        return post(url, r -> r);
    }

    String post(String url, Function<Request.Builder, Request.Builder> decorator) throws IOException
    {
        RequestBody body = RequestBody.create("", JSON);
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(body);
        Request request = decorator.apply(post).build();
        try (Response response = client.newCall(request).execute())
        {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
