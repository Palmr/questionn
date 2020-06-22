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
import static org.hamcrest.Matchers.equalTo;


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
        String result = post("http://localhost:5050/api/answers/sales_total");
        assertThat(result, containsString("12.72"));
    }

    @Test
    public void queryTotalSalesByCustomer() throws IOException
    {
        String result = post("http://localhost:5050/api/answers/sales_total_by_customer");
        assertThat(result, containsString("\"Alice Brown\",\"5.34\""));
    }

    @Test
    public void requestNonExistentAnswer() throws IOException
    {
        final Request.Builder post = new Request.Builder()
                .url("http://localhost:5050/api/answers/moose")
                .post(RequestBody.create("{}", JSON));
        final Request request = post.build();
        try (Response response = client.newCall(request).execute())
        {
            String result = Objects.requireNonNull(response.body()).string();
            assertThat(result, containsString("Answer not found: moose"));
            assertThat(response.code(), equalTo(404));
        }
    }

    @Test
    public void queryTotalSalesByCustomerAsCsv() throws IOException
    {
        String result = post("http://localhost:5050/api/answers/sales_total_by_customer",
                r -> r.header("Accept", "text/csv"), "{}");
        assertThat(result, containsString(
                "CUSTOMER_NAME,SALES_TOTAL\n" +
                "Alice Brown,5.34\n" +
                "Chris Dickinson,5.88\n" +
                "Gareth Hughes,1.50"
        ));
    }

    @Test
    public void queryTopCustomers() throws IOException
    {
        String result = post("http://localhost:5050/api/answers/top_k_customers",
                r -> r.header("Accept", "text/csv"), "{ \"limit\": 2 }");
        assertThat(result, containsString(
                "CUSTOMER_NAME,SALES_TOTAL\n" +
                "Chris Dickinson,5.88\n" +
                "Alice Brown,5.34"
        ));
    }

    String post(String url) throws IOException
    {
        return post(url, r -> r, "{}");
    }

    String post(
            String url,
            Function<Request.Builder, Request.Builder> decorator,
            String body
    ) throws IOException
    {
        final Request.Builder post = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body, JSON));
        final Request request = decorator.apply(post).build();
        try (Response response = client.newCall(request).execute())
        {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
