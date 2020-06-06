package org.example.questionn.testing;

public final class CreateTestingDbRequest
{
    public String description;

    public CreateTestingDbRequest()
    {
    }

    public CreateTestingDbRequest(final String description)
    {
        this.description = description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }
}
