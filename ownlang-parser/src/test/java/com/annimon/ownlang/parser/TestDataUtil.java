package com.annimon.ownlang.parser;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public class TestDataUtil {

    static Stream<File> scanDirectory(String dirPath) {
        return scanDirectory(new File(dirPath));
    }

    static Stream<File> scanDirectory(File dir) {
        final File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return Stream.empty();
        }
        return Arrays.stream(files)
                .flatMap(f -> f.isDirectory() ? scanDirectory(f) : Stream.of(f))
                .filter(f -> f.getName().endsWith(".own"));
    }
}
