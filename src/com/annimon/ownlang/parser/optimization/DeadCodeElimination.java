package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.parser.ast.ExprStatement;
import com.annimon.ownlang.parser.ast.IfStatement;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.TernaryExpression;
import com.annimon.ownlang.parser.ast.WhileStatement;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isValue;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isValueAsInt;

/**
 * Performs dead code elimination.
 */
public class DeadCodeElimination extends OptimizationVisitor<Void> implements Optimizable {

    private int ifStatementEliminatedCount;
    private int ternaryExpressionEliminatedCount;
    private int whileStatementEliminatedCount;

    @Override
    public Node optimize(Node node) {
        return node.accept(this, null);
    }

    @Override
    public int optimizationsCount() {
        return ifStatementEliminatedCount + ternaryExpressionEliminatedCount
                + whileStatementEliminatedCount;
    }

    @Override
    public String summaryInfo() {
        if (optimizationsCount() == 0) return "";
        final StringBuilder sb = new StringBuilder();
        if (ifStatementEliminatedCount > 0) {
            sb.append("\nEliminated IfStatement: ").append(ifStatementEliminatedCount);
        }
        if (ternaryExpressionEliminatedCount > 0) {
            sb.append("\nEliminated TernaryExpression: ").append(ternaryExpressionEliminatedCount);
        }
        if (whileStatementEliminatedCount > 0) {
            sb.append("\nEliminated WhileStatement: ").append(whileStatementEliminatedCount);
        }
        return sb.toString();
    }

    @Override
    public Node visit(IfStatement s, Void t) {
        if (isValue(s.expression)) {
            ifStatementEliminatedCount++;
            // true statement
            if (s.expression.eval().asInt() != 0) {
                return s.ifStatement;
            }
            // false statement
            if (s.elseStatement != null) {
                return s.elseStatement;
            }
            return new ExprStatement(s.expression);
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(TernaryExpression s, Void t) {
        if (isValue(s.condition)) {
            ternaryExpressionEliminatedCount++;
            if (s.condition.eval().asInt() != 0) {
                return s.trueExpr;
            }
            return s.falseExpr;
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(WhileStatement s, Void t) {
        if (isValueAsInt(s.condition, 0)) {
            whileStatementEliminatedCount++;
            return new ExprStatement(s.condition);
        }
        return super.visit(s, t);
    }
}
