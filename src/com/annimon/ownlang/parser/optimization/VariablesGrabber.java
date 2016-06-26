package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.parser.ast.Argument;
import com.annimon.ownlang.parser.ast.AssignmentExpression;
import com.annimon.ownlang.parser.ast.DestructuringAssignmentStatement;
import com.annimon.ownlang.parser.ast.FunctionDefineStatement;
import com.annimon.ownlang.parser.ast.MatchExpression;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.ValueExpression;
import com.annimon.ownlang.parser.ast.VariableExpression;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isValue;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isVariable;
import java.util.HashMap;
import java.util.Map;

public class VariablesGrabber extends OptimizationVisitor<Map<String, VariableInfo>> {

    public static Map<String, VariableInfo> getInfo(Node node) {
        Map<String, VariableInfo> variableInfos = new HashMap<>();
        node.accept(new VariablesGrabber(), variableInfos);
        return variableInfos;
    }

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