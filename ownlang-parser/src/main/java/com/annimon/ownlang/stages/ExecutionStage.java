package com.annimon.ownlang.stages;

import com.annimon.ownlang.parser.ast.Statement;

public class ExecutionStage implements Stage<Statement, Statement> {

    @Override
    public Statement perform(StagesData stagesData, Statement input) {
        input.execute();
        return input;
    }
}
