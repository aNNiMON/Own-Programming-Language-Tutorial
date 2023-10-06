package com.annimon.ownlang.util;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import com.annimon.ownlang.util.input.SourceLoaderStage;
import java.util.HashSet;
import static com.annimon.ownlang.util.SourceLocationFormatterStage.printPosition;

public class ErrorsLocationFormatterStage implements Stage<Iterable<? extends SourceLocatedError>, String> {
    public static final String TAG_POSITIONS = "formattedPositions";

    @Override
    public String perform(StagesData stagesData, Iterable<? extends SourceLocatedError> input) {
        final var sb = new StringBuilder();
        final var lines = stagesData.getOrDefault(SourceLoaderStage.TAG_SOURCE_LINES, new String[0]);
        for (SourceLocatedError error : input) {
            sb.append(Console.newline());
            sb.append(error);
            sb.append(Console.newline());
            final Range range = error.getRange();
            if (range != null && lines.length > 0) {
                var positions = stagesData.getOrDefault(TAG_POSITIONS, HashSet::new);
                positions.add(range);
                stagesData.put(TAG_POSITIONS, positions);
                printPosition(sb, range.normalize(), lines);
            }
        }
        return sb.toString();
    }
}