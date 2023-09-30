package com.annimon.ownlang.stages;

import com.annimon.ownlang.parser.Linter;
import com.annimon.ownlang.parser.ast.Statement;

public class LinterStage implements Stage<Statement, Statement> {

    @Override
    public Statement perform(StagesData stagesData, Statement input) {
        Linter.lint(input);
        return input;
    }
}
