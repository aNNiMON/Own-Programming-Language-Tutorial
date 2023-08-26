package com.annimon.ownlang.parser;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.optimization.ConstantFolding;
import com.annimon.ownlang.parser.optimization.ConstantPropagation;
import com.annimon.ownlang.parser.optimization.DeadCodeElimination;
import com.annimon.ownlang.parser.optimization.ExpressionSimplification;
import com.annimon.ownlang.parser.optimization.InstructionCombining;
import com.annimon.ownlang.parser.optimization.Optimizable;
import com.annimon.ownlang.parser.optimization.SummaryOptimization;

public final class Optimizer {

    private Optimizer() { }

    public static Statement optimize(Statement statement, int level, boolean showSummary) {
        if (level == 0) return statement;

        final Optimizable optimization = new SummaryOptimization(new Optimizable[] {
            new ConstantFolding(),
            new ConstantPropagation(),
            new DeadCodeElimination(),
            new ExpressionSimplification(),
            new InstructionCombining()
        });

        Node result = statement;
        if (level >= 9) {
            int iteration = 0, lastModifications = 0;
            do {
                lastModifications = optimization.optimizationsCount();
                result = optimization.optimize(result);
                iteration++;
            } while (lastModifications != optimization.optimizationsCount());
            if (showSummary)
                Console.print("Performs " + iteration + " optimization iterations");
        } else {
            for (int i = 0; i < level; i++) {
                result = optimization.optimize(result);
            }
        }
        if (showSummary) {
            Console.println(optimization.summaryInfo());
        }
        return (Statement) result;
    }
}
