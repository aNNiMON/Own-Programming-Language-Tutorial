package com.annimon.ownlang.util.input;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SourceLoaderStage implements Stage<InputSource, String> {

    public static final String TAG_SOURCE_LINES = "sourceLines";

    @Override
    public String perform(StagesData stagesData, InputSource inputSource) {
        try {
            String result = inputSource.load();
            final var lines = (result == null || result.isEmpty())
                    ? new String[0]
                    : result.split("\r?\n");
            stagesData.put(TAG_SOURCE_LINES, lines);
            return result;
        } catch (IOException e) {
            throw new OwnLangRuntimeException("Unable to read input " + inputSource, e);
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