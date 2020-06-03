package org.example.questionn.answers;

public class AnswerDetail {
    public final String name;
    public final String title;
    public final String url;

    public AnswerDetail(final Answer answer) {
        this.name = answer.name;
        this.title = answer.title;
        this.url = "/api/answers/" + answer.name; // TODO - urlencode or something...
    }
}
