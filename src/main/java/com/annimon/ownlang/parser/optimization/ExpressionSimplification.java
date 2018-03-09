package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.parser.ast.BinaryExpression;
import com.annimon.ownlang.parser.ast.ConditionalExpression;
import com.annimon.ownlang.parser.ast.FunctionDefineStatement;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.UnaryExpression;
import com.annimon.ownlang.parser.ast.ValueExpression;
import com.annimon.ownlang.parser.visitors.VisitorUtils;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isIntegerValue;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isSameVariables;
import java.util.HashSet;
import java.util.Set;

/**
 * Performs expression simplification.
 */
public class ExpressionSimplification extends OptimizationVisitor<Void> implements Optimizable {

    private static final Set<String> OPERATORS = VisitorUtils.operators();

    private int simplificationsCount;

    private final Set<String> overloadedOperators;

    public ExpressionSimplification() {
        simplificationsCount = 0;
        overloadedOperators = new HashSet<>();
    }

    @Override
    public Node optimize(Node node) {
        return node.accept(this, null);
    }

    @Override
    public int optimizationsCount() {
        return simplificationsCount;
    }

    @Override
    public String summaryInfo() {
        if (optimizationsCount() == 0) return "";
        final StringBuilder sb = new StringBuilder();
        if (simplificationsCount > 0) {
            sb.append("\nExpression simplifications: ").append(simplificationsCount);
        }
        return sb.toString();
    }

    @Override
    public Node visit(BinaryExpression s, Void t) {
        if (overloadedOperators.contains(s.operation.toString())) {
            return super.visit(s, t);
        }
        // operations with 0
        final boolean expr1IsZero = isIntegerValue(s.expr1, 0);
        if (expr1IsZero || isIntegerValue(s.expr2, 0)) {
            switch (s.operation) {
                case ADD:
                    //  0 + x2 to x2, x1 + 0 to x1
                    simplificationsCount++;
                    return expr1IsZero ? s.expr2 : s.expr1;

                case SUBTRACT:
                    simplificationsCount++;
                    if (expr1IsZero) {
                        // 0 - x2 to -x2
                        return new UnaryExpression(UnaryExpression.Operator.NEGATE, s.expr2);
                    }
                    // x1 - 0 to x1
                    return s.expr1;

                case MULTIPLY:
                    // 0 * x2 to 0, x1 * 0 to 0
                    simplificationsCount++;
                    return new ValueExpression(0);

                case DIVIDE:
                    // 0 / x2 to 0
                    if (expr1IsZero) {
                        simplificationsCount++;
                        return new ValueExpression(0);
                    }
                    break;
            }
        }

        // operations with 1
        final boolean expr1IsOne = isIntegerValue(s.expr1, 1);
        if (expr1IsOne || isIntegerValue(s.expr2, 1)) {
            switch (s.operation) {
                case MULTIPLY:
                    // 1 * x2 to x2, x1 * 1 to x1
                    simplificationsCount++;
                    return expr1IsOne ? s.expr2 : s.expr1;

                case DIVIDE:
                    // x1 / 1 to x1
                    if (!expr1IsOne) {
                        simplificationsCount++;
                        return s.expr1;
                    }
                    break;
            }
        }

        // x1 / -1 to -x1
        if (isIntegerValue(s.expr2, -1)) {
            simplificationsCount++;
            return new UnaryExpression(UnaryExpression.Operator.NEGATE, s.expr1);
        }

        // x - x to 0
        if (isSameVariables(s.expr1, s.expr2) && s.operation == BinaryExpression.Operator.SUBTRACT) {
            simplificationsCount++;
            return new ValueExpression(0);
        }

        // x >> 0 to x, x << 0 to x
        if (isIntegerValue(s.expr2, 0) &&
                (s.operation == BinaryExpression.Operator.LSHIFT ||
                 s.operation == BinaryExpression.Operator.RSHIFT)) {
            simplificationsCount++;
            return s.expr1;
        }

        return super.visit(s, t);
    }

    @Override
    public Node visit(ConditionalExpression s, Void t) {
        if (overloadedOperators.contains(s.operation.getName())) {
            return super.visit(s, t);
        }
        if (isIntegerValue(s.expr1, 0) && s.operation == ConditionalExpression.Operator.AND) {
            // 0 && x2 to 0
            simplificationsCount++;
            return new ValueExpression(0);
        }
        if (isIntegerValue(s.expr1, 1) && s.operation == ConditionalExpression.Operator.OR) {
            // 1 || x2 to 1
            simplificationsCount++;
            return new ValueExpression(1);
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(FunctionDefineStatement s, Void t) {
        if (OPERATORS.contains(s.name)) {
            overloadedOperators.add(s.name);
        }
        return super.visit(s, t);
    }
}
