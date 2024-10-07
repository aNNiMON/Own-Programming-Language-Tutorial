package com.annimon.ownlang.util.input;

import java.nio.file.Files;
import java.nio.file.Path;

public record InputSourceDetector(String basePath) {
    public static final String RESOURCE_PREFIX = "resource:";

    public InputSourceDetector() {
        this("");
    }

    public boolean isReadable(String programPath) {
        if (programPath.startsWith(RESOURCE_PREFIX)) {
            String path = programPath.substring(RESOURCE_PREFIX.length());
            return getClass().getResource(basePath + path) != null;
        } else {
            Path path = Path.of(basePath, programPath);
            return Files.isReadable(path) && Files.isRegularFile(path);
        }
    }

    public InputSource toInputSource(String programPath) {
        if (programPath.startsWith(RESOURCE_PREFIX)) {
            String path = programPath.substring(RESOURCE_PREFIX.length());
            return new InputSourceResource(basePath + path);
        } else {
            return new InputSourceFile(basePath + programPath);
        }
    }
}
