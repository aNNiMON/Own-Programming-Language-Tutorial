package com.annimon.ownlang.parser;

import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.ConstantFolding;
import com.annimon.ownlang.parser.visitors.DeadCodeElimination;

public final class Optimizer {

    public interface Info {

        int optimizationsCount();

        String summaryInfo();
    }

    public static Statement optimize(Statement statement, int level) {
        if (level == 0) return statement;
        
        final ConstantFolding constantFolding = new ConstantFolding();
        final DeadCodeElimination deadCodeElimination = new DeadCodeElimination();

        Statement result = statement;
        for (int i = 0; i < level; i++) {
            result = (Statement) result.accept(constantFolding, null);
            result = (Statement) result.accept(deadCodeElimination, null);
        }
        System.out.print(constantFolding.summaryInfo());
        System.out.print(deadCodeElimination.summaryInfo());
        System.out.println();
        return result;
    }
}
