package com.annimon.ownlang.util.input;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public record InputSourceFile(String path) implements InputSource {

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String load() throws IOException {
        if (Files.isReadable(Path.of(path))) {
            try (InputStream is = new FileInputStream(path)) {
                return SourceLoaderStage.readStream(is);
            }
        }
        throw new IOException(path + " not found");
    }

    @Override
    public String toString() {
        return "File " + path;
    }
}
