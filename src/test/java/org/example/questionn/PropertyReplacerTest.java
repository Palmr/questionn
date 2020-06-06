package org.example.questionn;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyReplacerTest
{
    @Test
    void replaceThings()
    {
        final Map<String, String> properties = new HashMap<>();
        properties.put("hello.world", "goodbye");

        final String result = PropertyReplacer.replaceProperties("something-moose=${hello.world}-whatever", properties);

        assertEquals("something-moose=goodbye-whatever", result);
    }
}
