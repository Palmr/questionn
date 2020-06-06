package org.example.questionn.testing;

import java.time.LocalDateTime;

public class DbEntry
{
    public final long id;
    public final String description;
    public final LocalDateTime createdDatetime;

    public DbEntry(
            final long id,
            final String description,
            final LocalDateTime createdDatetime
    )
    {
        this.id = id;
        this.description = description;
        this.createdDatetime = createdDatetime;
    }
}
