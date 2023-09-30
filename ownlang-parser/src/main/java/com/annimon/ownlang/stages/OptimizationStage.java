package com.annimon.ownlang.stages;

import com.annimon.ownlang.parser.Optimizer;
import com.annimon.ownlang.parser.ast.Statement;

public record OptimizationStage(int level)
        implements Stage<Statement, Statement> {

    public static final String TAG_OPTIMIZATION_SUMMARY = "optimizationSummary";

    @Override
    public Statement perform(StagesData stagesData, Statement input) {
        boolean showSummary = stagesData.get(TAG_OPTIMIZATION_SUMMARY);
        return Optimizer.optimize(input, level, showSummary);
    }
}
