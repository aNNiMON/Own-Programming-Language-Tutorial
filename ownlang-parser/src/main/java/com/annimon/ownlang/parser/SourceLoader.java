package com.annimon.ownlang.parser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class SourceLoader {

    private SourceLoader() { }

    public static String readSource(String name) throws IOException {
        InputStream is = SourceLoader.class.getResourceAsStream("/" + name);
        if (is != null) return readAndCloseStream(is);

        is = new FileInputStream(name);
        return readAndCloseStream(is);
    }
    
    public static String readAndCloseStream(InputStream is) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final int bufferSize = 1024;
        final byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = is.read(buffer)) != -1) {
            result.write(buffer, 0, read);
        }
        is.close();
        return result.toString(StandardCharsets.UTF_8);
    }
}
