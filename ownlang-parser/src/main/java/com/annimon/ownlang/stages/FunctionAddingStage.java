package com.annimon.ownlang.stages;

import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.FunctionAdder;

public class FunctionAddingStage implements Stage<Statement, Statement> {

    @Override
    public Statement perform(StagesData stagesData, Statement input) {
        input.accept(new FunctionAdder());
        return input;
    }
}
