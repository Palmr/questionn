package org.example.questionn;

public interface Csv
{
    String[] fieldNames();
    Iterable<String[]> records();
}
