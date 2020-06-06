package org.example.questionn;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PropertyReplacer
{
    private PropertyReplacer()
    {
    }

    static String replaceProperties(
            String configValue,
            Map<String, String> properties)
    {
        final Pattern pattern = Pattern.compile("(\\$\\{\\s*([\\w|\\-.]+)\\s*})");
        final Matcher matcher = pattern.matcher(configValue);
        String result = configValue;
        while (matcher.find())
        {
            final String group = matcher.group(1);
            final String propertyKey = matcher.group(2);
            result = result.replace(group, properties.get(propertyKey));
        }
        return result;
    }
}
