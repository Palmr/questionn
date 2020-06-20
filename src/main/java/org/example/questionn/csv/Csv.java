package org.example.questionn.csv;

import java.util.Collections;

import org.reactivestreams.Publisher;


import ratpack.stream.Streams;

import static java.util.Arrays.asList;

public interface Csv
{
    String[] fieldNames();

    Iterable<String[]> records();

    default Publisher<String> toStreamingCsv()
    {
        Publisher<String> headerPublisher = Streams.publish(Collections.singletonList(csvRowUnsafely(fieldNames())));
        Publisher<String> recordPublisher = Streams.publish(records()).map(this::csvRowUnsafely);

        return Streams.concat(asList(headerPublisher, recordPublisher));
    }

    // TODO - Add some CSV safety, i.e. escaping
    default String csvRowUnsafely(String[] strings)
    {
        return String.join(",", strings).concat("\n");
    }
}
