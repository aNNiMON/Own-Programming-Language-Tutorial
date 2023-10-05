package com.annimon.ownlang;

import com.annimon.ownlang.util.input.InputSource;
import com.annimon.ownlang.util.input.InputSourceFile;
import com.annimon.ownlang.util.input.InputSourceProgram;
import com.annimon.ownlang.util.input.InputSourceResource;
import java.nio.file.Files;
import java.nio.file.Path;

public class RunOptions {
    private static final String RESOURCE_PREFIX = "resource:";
    private static final String DEFAULT_PROGRAM = "program.own";

    // input
    String programPath;
    String programSource;
    // modes
    boolean lintMode;
    boolean beautifyMode;
    int optimizationLevel;
    // flags
    boolean showTokens;
    boolean showAst;
    boolean showMeasurements;

    String detectDefaultProgramPath() {
        if (getClass().getResource("/" + DEFAULT_PROGRAM) != null) {
            return RESOURCE_PREFIX + "/" + DEFAULT_PROGRAM;
        }
        if (Files.isReadable(Path.of(DEFAULT_PROGRAM))) {
            return DEFAULT_PROGRAM;
        }
        return null;
    }

    InputSource toInputSource() {
        if (programSource != null) {
            return new InputSourceProgram(programSource);
        }
        if (programPath == null) {
            // No arguments. Default to program.own
            programPath = detectDefaultProgramPath();
            if (programPath == null) {
                throw new IllegalArgumentException("Empty input");
            }
        }

        if (programPath.startsWith(RESOURCE_PREFIX)) {
            String path = programPath.substring(RESOURCE_PREFIX.length());
            return new InputSourceResource(path);
        } else {
            return new InputSourceFile(programPath);
        }
    }
}
