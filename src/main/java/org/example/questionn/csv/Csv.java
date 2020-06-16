package org.example.questionn.csv;

public interface Csv
{
    String[] fieldNames();
    Iterable<String[]> records();

    default String toCsv()
    {
        // Deliberately unescaped so far.
        // A proper csv implementation should: a.) stream and b.) escape appropriately
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.join(",", fieldNames()));
        for (String[] record : records())
        {
            stringBuilder.append('\n');
            stringBuilder.append(String.join(",", record));
        }

        return stringBuilder.toString();
    }
}
