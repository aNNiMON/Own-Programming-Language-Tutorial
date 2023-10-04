package com.annimon.ownlang.util;

import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;

public class ExceptionConverterStage implements Stage<Exception, SourceLocatedError> {
    @Override
    public SourceLocatedError perform(StagesData stagesData, Exception ex) {
        if (ex instanceof SourceLocatedError sle) return sle;
        return new SimpleError(ex.getMessage());
    }
}
