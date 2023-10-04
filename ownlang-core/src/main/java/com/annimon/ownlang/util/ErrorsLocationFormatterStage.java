package com.annimon.ownlang.util;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;

public class ErrorsLocationFormatterStage implements Stage<Iterable<? extends SourceLocatedError>, String> {

    @Override
    public String perform(StagesData stagesData, Iterable<? extends SourceLocatedError> input) {
        final var sb = new StringBuilder();
        final String source = stagesData.get(SourceLoaderStage.TAG_SOURCE);
        final var lines = source.split("\r?\n");
        for (SourceLocatedError error : input) {
            sb.append(Console.newline());
            sb.append(error);
            sb.append(Console.newline());
            final Range range = error.getRange();
            if (range != null) {
                printPosition(sb, range.normalize(), lines);
            }
        }
        return sb.toString();
    }

    private static void printPosition(StringBuilder sb, Range range, String[] lines) {
        final Pos start = range.start();
        final int linesCount = lines.length;;
        if (range.isOnSameLine()) {
            if (start.row() < linesCount) {
                sb.append(lines[start.row()]);
                sb.append(Console.newline());
                sb.append(" ".repeat(start.col()));
                sb.append("^".repeat(range.end().col() - start.col() + 1));
                sb.append(Console.newline());
            }
        } else {
            if (start.row() < linesCount) {
                String line = lines[start.row()];
                sb.append(line);
                sb.append(Console.newline());
                sb.append(" ".repeat(start.col()));
                sb.append("^".repeat(Math.max(1, line.length() - start.col())));
                sb.append(Console.newline());
            }
            final Pos end = range.end();
            if (end.row() < linesCount) {
                sb.append(lines[end.row()]);
                sb.append(Console.newline());
                sb.append("^".repeat(end.col()));
                sb.append(Console.newline());
            }
        }
    }
}