package com.annimon.ownlang.util;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SourceLoaderStage implements Stage<String, String> {

    public static final String TAG_SOURCE = "source";

    @Override
    public String perform(StagesData stagesData, String name) {
        try {
            String result = readSource(name);
            stagesData.put(TAG_SOURCE, result);
            return result;
        } catch (IOException e) {
            throw new OwnLangRuntimeException("Unable to read input " + name, e);
        }
    }

    private String readSource(String name) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/" + name)) {
            if (is != null) {
                return readStream(is);
            }
        }
        try (InputStream is = new FileInputStream(name)) {
            return readStream(is);
        }
    }

    public static String readStream(InputStream is) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final int bufferSize = 1024;
        final byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = is.read(buffer)) != -1) {
            result.write(buffer, 0, read);
        }
        return result.toString(StandardCharsets.UTF_8);
    }
}