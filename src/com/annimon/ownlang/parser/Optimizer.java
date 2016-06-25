package com.annimon.ownlang.parser;

import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.ConstantFolding;
import com.annimon.ownlang.parser.visitors.ConstantPropagation;
import com.annimon.ownlang.parser.visitors.DeadCodeElimination;
import com.annimon.ownlang.parser.visitors.ExpressionSimplification;

public final class Optimizer {

    public interface Info {

        int optimizationsCount();

        String summaryInfo();
    }

    public static Statement optimize(Statement statement, int level) {
        if (level == 0) return statement;
        
        final ConstantFolding constantFolding = new ConstantFolding();
        final ConstantPropagation constantPropagation = new ConstantPropagation();
        final DeadCodeElimination deadCodeElimination = new DeadCodeElimination();
        final ExpressionSimplification expressionSimplification = new ExpressionSimplification();

        Statement result = statement;
        for (int i = 0; i < level; i++) {
            result = (Statement) result.accept(constantFolding, null);
            result = (Statement) constantPropagation.visit(result);
            result = (Statement) result.accept(deadCodeElimination, null);
            result = (Statement) result.accept(expressionSimplification, null);
        }
        System.out.print(constantFolding.summaryInfo());
        System.out.print(constantPropagation.summaryInfo());
        System.out.print(deadCodeElimination.summaryInfo());
        System.out.print(expressionSimplification.summaryInfo());
        System.out.println();
        return result;
    }
}
