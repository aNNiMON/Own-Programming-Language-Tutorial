package com.annimon.ownlang.parser.error;

import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import com.annimon.ownlang.util.ErrorsLocationFormatterStage;
import com.annimon.ownlang.util.ErrorsStackTraceFormatterStage;
import com.annimon.ownlang.util.SourceLocatedError;
import java.util.Collection;

public class ParseErrorsFormatterStage implements Stage<Collection<? extends SourceLocatedError>, String> {

    @Override
    public String perform(StagesData stagesData, Collection<? extends SourceLocatedError> input) {
        String error = new ErrorsLocationFormatterStage()
                .perform(stagesData, input);
        String stackTrace = new ErrorsStackTraceFormatterStage()
                .perform(stagesData, input);
        return error + "\n" + stackTrace;
    }
}
