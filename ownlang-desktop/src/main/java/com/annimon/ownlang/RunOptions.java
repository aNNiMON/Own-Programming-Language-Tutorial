package com.annimon.ownlang;

import com.annimon.ownlang.parser.linters.LinterStage;
import com.annimon.ownlang.util.input.*;
import static com.annimon.ownlang.util.input.InputSourceDetector.RESOURCE_PREFIX;

public class RunOptions {
    private static final String DEFAULT_PROGRAM = "program.own";

    // input
    String programPath;
    String programSource;
    // modes
    LinterStage.Mode lintMode = LinterStage.Mode.SEMANTIC;
    boolean beautifyMode;
    int optimizationLevel;
    // flags
    boolean showTokens;
    boolean showAst;
    boolean showMeasurements;

    private final InputSourceDetector inputSourceDetector = new InputSourceDetector();

    boolean linterEnabled() {
        return lintMode != null && lintMode != LinterStage.Mode.NONE;
    }

    String detectDefaultProgramPath() {
        final String resourcePath = RESOURCE_PREFIX + "/" + DEFAULT_PROGRAM;
        if (inputSourceDetector.isReadable(resourcePath)) {
            return resourcePath;
        }
        if (inputSourceDetector.isReadable(DEFAULT_PROGRAM)) {
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
        return inputSourceDetector.toInputSource(programPath);
    }
}
