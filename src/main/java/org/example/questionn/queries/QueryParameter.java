package org.example.questionn.queries;

public final class QueryParameter
{
    public String name;
    public String displayName;
    public String type;
    public String generator;
    public boolean optional;

    @Override
    public String toString()
    {
        return "QueryParameter{" +
               "name='" + name + '\'' +
               ", displayName='" + displayName + '\'' +
               ", type='" + type + '\'' +
               ", generator='" + generator + '\'' +
               ", optional=" + optional +
               '}';
    }
}
