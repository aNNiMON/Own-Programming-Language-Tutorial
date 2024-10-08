package com.annimon.ownlang.util.input;

import java.nio.file.Files;
import java.nio.file.Path;

public record InputSourceDetector(String basePath) {
    public static final String RESOURCE_PREFIX = "resource:";

    public InputSourceDetector() {
        this("");
    }

    public boolean isReadable(String programPath) {
        if (basePath.startsWith(RESOURCE_PREFIX) || programPath.startsWith(RESOURCE_PREFIX)) {
            String base = removePrefixIfExists(basePath);
            String path = removePrefixIfExists(programPath);
            return getClass().getResource(base + path) != null;
        } else {
            Path path = Path.of(basePath, programPath);
            return Files.isReadable(path) && Files.isRegularFile(path);
        }
    }

    public InputSource toInputSource(String programPath) {
        if (basePath.startsWith(RESOURCE_PREFIX) || programPath.startsWith(RESOURCE_PREFIX)) {
            String base = removePrefixIfExists(basePath);
            String path = removePrefixIfExists(programPath);
            return new InputSourceResource(base + path);
        } else {
            return new InputSourceFile(basePath + programPath);
        }
    }

    private String removePrefixIfExists(String path) {
        if (path.startsWith(RESOURCE_PREFIX)) {
            return path.substring(RESOURCE_PREFIX.length());
        }
        return path;
    }
}
