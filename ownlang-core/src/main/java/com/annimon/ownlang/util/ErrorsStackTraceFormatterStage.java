package com.annimon.ownlang.util;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;

public class ErrorsStackTraceFormatterStage implements Stage<Iterable<? extends SourceLocatedError>, String> {

    @Override
    public String perform(StagesData stagesData, Iterable<? extends SourceLocatedError> input) {
        final var sb = new StringBuilder();
        for (SourceLocatedError error : input) {
            if (!error.hasStackTrace()) continue;
            for (StackTraceElement el : error.getStackTrace()) {
                sb.append("\t").append(el).append(Console.newline());
            }
        }
        return sb.toString();
    }
}