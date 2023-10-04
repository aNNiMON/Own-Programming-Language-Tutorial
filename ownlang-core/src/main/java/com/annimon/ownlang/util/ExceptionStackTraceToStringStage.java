package com.annimon.ownlang.util;

import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ExceptionStackTraceToStringStage implements Stage<Exception, String> {
    @Override
    public String perform(StagesData stagesData, Exception ex) {
        final var baos = new ByteArrayOutputStream();
        try (final PrintStream ps = new PrintStream(baos)) {
            for (StackTraceElement traceElement : ex.getStackTrace()) {
                ps.println("\tat " + traceElement);
            }
        }
        return baos.toString(StandardCharsets.UTF_8);
    }
}
