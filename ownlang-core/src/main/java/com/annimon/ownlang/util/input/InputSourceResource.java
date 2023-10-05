package com.annimon.ownlang.util.input;

import java.io.IOException;
import java.io.InputStream;

public record InputSourceResource(String path) implements InputSource {

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String load() throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                return SourceLoaderStage.readStream(is);
            }
        }
        throw new IOException(path + " not found");
    }

    @Override
    public String toString() {
        return "Resource " + path;
    }
}
