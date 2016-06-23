package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.Optimizer;
import com.annimon.ownlang.parser.ast.BinaryExpression;
import com.annimon.ownlang.parser.ast.ConditionalExpression;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.UnaryExpression;
import com.annimon.ownlang.parser.ast.ValueExpression;
import com.annimon.ownlang.parser.ast.VariableExpression;

/**
 * Performs expression simplification.
 */
public class ExpressionSimplification extends OptimizationVisitor<Void> implements Optimizer.Info {

    private int simplificationsCount;

    public ExpressionSimplification() {
        simplificationsCount = 0;
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

        return super.visit(s, t);
    }

    @Override
    public Node visit(ConditionalExpression s, Void t) {
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


    private boolean isIntegerValue(Node node, int valueToCheck) {
        if (!(node instanceof ValueExpression)) return false;

        final Value value = ((ValueExpression) node).value;
        if (value.type() != Types.NUMBER) return false;

        final Number number = ((NumberValue) value).raw();
        if ( (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte)) {
            return number.intValue() == valueToCheck;
        }
        return false;
    }

    private boolean isSameVariables(Node n1, Node n2) {
        if ( (n1 instanceof VariableExpression) && (n2 instanceof VariableExpression) ) {
            final VariableExpression v1 = (VariableExpression) n1;
            final VariableExpression v2 = (VariableExpression) n2;
            return v1.name.equals(v2.name);
        }
        return false;
    }
}
