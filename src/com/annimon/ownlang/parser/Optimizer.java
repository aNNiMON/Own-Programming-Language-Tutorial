package com.annimon.ownlang.parser;

import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.optimization.ConstantFolding;
import com.annimon.ownlang.parser.optimization.ConstantPropagation;
import com.annimon.ownlang.parser.optimization.DeadCodeElimination;
import com.annimon.ownlang.parser.optimization.ExpressionSimplification;
import com.annimon.ownlang.parser.optimization.Optimizable;
import com.annimon.ownlang.parser.optimization.SummaryOptimization;

public final class Optimizer {

    public static Statement optimize(Statement statement, int level) {
        if (level == 0) return statement;

        final Optimizable optimization = new SummaryOptimization(new Optimizable[] {
            new ConstantFolding(),
            new ConstantPropagation(),
            new DeadCodeElimination(),
            new ExpressionSimplification()
        });

        Node result = statement;
        for (int i = 0; i < level; i++) {
            result = optimization.optimize(result);
        }
        System.out.println(optimization.summaryInfo());
        return (Statement) result;
    }
}
