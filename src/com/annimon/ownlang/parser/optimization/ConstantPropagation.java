package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.Optimizer;
import com.annimon.ownlang.parser.ast.Argument;
import com.annimon.ownlang.parser.ast.AssignmentExpression;
import com.annimon.ownlang.parser.ast.DestructuringAssignmentStatement;
import com.annimon.ownlang.parser.ast.FunctionDefineStatement;
import com.annimon.ownlang.parser.ast.MatchExpression;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.ValueExpression;
import com.annimon.ownlang.parser.ast.VariableExpression;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isValue;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Performs constant propagation.
 */
public class ConstantPropagation implements Optimizer.Info {

    private final Map<String, Integer> propagatedVariables;

    public ConstantPropagation() {
        propagatedVariables = new HashMap<>();
    }

    @Override
    public int optimizationsCount() {
        return propagatedVariables.size();
    }

    @Override
    public String summaryInfo() {
        if (optimizationsCount() == 0) return "";
        final StringBuilder sb = new StringBuilder();
        if (propagatedVariables.size() > 0) {
            sb.append("\nConstant propagations: ").append(propagatedVariables.size());
            for (Map.Entry<String, Integer> e : propagatedVariables.entrySet()) {
                sb.append("\n  ").append(e.getKey()).append(": ").append(e.getValue());
            }
        }
        return sb.toString();
    }

    public Node visit(Statement s) {
        final Map<String, VariableInfo> variables = new HashMap<>();
        // Find variables
        s.accept(new VariablesGrabber(), variables);
        // Filter only string/number values with 1 modification
        final Map<String, Value> candidates = new HashMap<>();
        for (Map.Entry<String, VariableInfo> e : variables.entrySet()) {
            final VariableInfo info = e.getValue();
            if (info.modifications != 1) continue;
            if (info.value == null) continue;
            switch (info.value.type()) {
                case Types.NUMBER:
                case Types.STRING:
                    candidates.put(e.getKey(), info.value);
                    break;
            }
        }
        // Replace VariableExpression with ValueExpression
        return s.accept(new VariablesPropagator(), candidates);
    }

    private class VariablesGrabber extends OptimizationVisitor<Map<String, VariableInfo>> {

        @Override
        public Node visit(AssignmentExpression s, Map<String, VariableInfo> t) {
            if (!isVariable((Node)s.target)) {
                return super.visit(s, t);
            }

            final String variableName = ((VariableExpression) s.target).name;
            final VariableInfo var = variableInfo(t, variableName);

            if (s.operation == null && isValue(s.expression)) {
                var.value = ((ValueExpression) s.expression).value;
            }
            t.put(variableName, var);
            return super.visit(s, t);
        }

        @Override
        public Node visit(DestructuringAssignmentStatement s, Map<String, VariableInfo> t) {
            for (String variableName : s.variables) {
                if (variableName == null) continue;
                t.put(variableName, variableInfo(t, variableName));
            }
            return super.visit(s, t);
        }

        @Override
        public Node visit(FunctionDefineStatement s, Map<String, VariableInfo> t) {
            for (Argument argument : s.arguments) {
                final String variableName = argument.getName();
                t.put(variableName, variableInfo(t, variableName));
            }
            return super.visit(s, t);
        }

        @Override
        public Node visit(MatchExpression s, Map<String, VariableInfo> t) {
            // no visit match expression
            return s;
        }

        private VariableInfo variableInfo(Map<String, VariableInfo> t, final String variableName) {
            final VariableInfo var;
            if (t.containsKey(variableName)) {
                var = t.get(variableName);
                var.modifications++;
            } else {
                var = new VariableInfo();
                var.modifications = 1;
            }
            return var;
        }
    }

    private class VariablesPropagator extends OptimizationVisitor<Map<String, Value>> {

        @Override
        public Node visit(VariableExpression s, Map<String, Value> t) {
            if (t.containsKey(s.name)) {
                if (!propagatedVariables.containsKey(s.name)) {
                    propagatedVariables.put(s.name, 1);
                } else {
                    propagatedVariables.put(s.name, 1 + propagatedVariables.get(s.name));
                }
                return new ValueExpression(t.get(s.name));
            }
            return super.visit(s, t);
        }
    }

    private static class VariableInfo {
        Value value;
        int modifications;

        @Override
        public String toString() {
            return (value == null ? "?" : value) + " (" + modifications + " mods)";
        }
    }
}
