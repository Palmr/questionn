package org.example.questionn.answers;

public class AnswerDetail
{
    public final String name;
    public final String title;
    public final String url;
    public final String description;

    public AnswerDetail(final Answer answer)
    {
        this.name = answer.name;
        this.title = answer.title;
        this.description = answer.description;
        this.url = "/api/answers/" + answer.name; // TODO - urlencode or something...
    }
}
