package com.annimon.ownlang.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class SourceLoader {

    public static String readSource(String name) throws IOException {
        InputStream is = SourceLoader.class.getResourceAsStream(name);
        if (is != null) return readStream(is);

        is = new FileInputStream(name);
        return readStream(is);
    }
    
    private static String readStream(InputStream is) throws IOException {
        final StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append(System.lineSeparator());
            }
        }
        return text.toString();
    }
}
