package com.annimon.ownlang.parser.error;

import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import com.annimon.ownlang.util.ErrorsLocationFormatterStage;
import com.annimon.ownlang.util.ErrorsStackTraceFormatterStage;

public class ParseErrorsFormatterStage implements Stage<ParseErrors, String> {

    @Override
    public String perform(StagesData stagesData, ParseErrors input) {
        String error = new ErrorsLocationFormatterStage()
                .perform(stagesData, input);
        String stackTrace = new ErrorsStackTraceFormatterStage()
                .perform(stagesData, input);
        return error + "\n" + stackTrace;
    }
}
