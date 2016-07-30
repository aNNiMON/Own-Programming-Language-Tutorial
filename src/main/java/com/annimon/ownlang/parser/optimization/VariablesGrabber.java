package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.parser.ast.Accessible;
import com.annimon.ownlang.parser.ast.Argument;
import com.annimon.ownlang.parser.ast.Arguments;
import com.annimon.ownlang.parser.ast.AssignmentExpression;
import com.annimon.ownlang.parser.ast.ContainerAccessExpression;
import com.annimon.ownlang.parser.ast.DestructuringAssignmentStatement;
import com.annimon.ownlang.parser.ast.ForeachArrayStatement;
import com.annimon.ownlang.parser.ast.ForeachMapStatement;
import com.annimon.ownlang.parser.ast.MatchExpression;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.UnaryExpression;
import com.annimon.ownlang.parser.ast.UseStatement;
import com.annimon.ownlang.parser.ast.ValueExpression;
import com.annimon.ownlang.parser.ast.VariableExpression;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isValue;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isVariable;
import java.util.HashMap;
import java.util.Map;

public class VariablesGrabber extends OptimizationVisitor<Map<String, VariableInfo>> {

    public static Map<String, VariableInfo> getInfo(Node node) {
        return getInfo(node, false);
    }

    public static Map<String, VariableInfo> getInfo(Node node, boolean grabModuleConstants) {
        Map<String, VariableInfo> variableInfos = new HashMap<>();
        node.accept(new VariablesGrabber(grabModuleConstants), variableInfos);
        return variableInfos;
    }

    private final boolean grabModuleConstants;

    public VariablesGrabber() {
        this(false);
    }

    public VariablesGrabber(boolean grabModuleConstants) {
        this.grabModuleConstants = grabModuleConstants;
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
    public Node visit(ForeachArrayStatement s, Map<String, VariableInfo> t) {
        t.put(s.variable, variableInfo(t, s.variable));
        return super.visit(s, t);
    }

    @Override
    public Node visit(ForeachMapStatement s, Map<String, VariableInfo> t) {
        t.put(s.key, variableInfo(t, s.key));
        t.put(s.value, variableInfo(t, s.value));
        return super.visit(s, t);
    }

    @Override
    public Node visit(MatchExpression s, Map<String, VariableInfo> t) {
        for (MatchExpression.Pattern pattern : s.patterns) {
            if (pattern instanceof MatchExpression.VariablePattern) {
                final String variableName = ((MatchExpression.VariablePattern) pattern).variable;
                t.put(variableName, variableInfo(t, variableName));
            }
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(UnaryExpression s, Map<String, VariableInfo> t) {
        if (s.expr1 instanceof Accessible) {
            if (s.expr1 instanceof VariableExpression) {
                final String variableName = ((VariableExpression) s.expr1).name;
                t.put(variableName, variableInfo(t, variableName));
            }
            if (s.expr1 instanceof ContainerAccessExpression) {
                final String variableName = ((ContainerAccessExpression) s.expr1).variable;
                t.put(variableName, variableInfo(t, variableName));
            }
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(UseStatement s, Map<String, VariableInfo> t) {
        if (grabModuleConstants) {
            // To get module variables we need  to store current variables, clear all, then load module.
            final Map<String, Value> currentVariables = new HashMap<>(Variables.variables());
            Variables.variables().clear();
            if (isValue(s.expression)) {
                s.loadConstants();
            }
            // Grab module variables
            for (Map.Entry<String, Value> entry : Variables.variables().entrySet()) {
                final VariableInfo var = variableInfo(t, entry.getKey());
                var.value = entry.getValue();
                t.put(entry.getKey(), var);
            }
            // Restore previous variables
            Variables.variables().putAll(currentVariables);
        }
        return super.visit(s, t);
    }

    @Override
    protected boolean visit(Arguments in, Arguments out, Map<String, VariableInfo> t) {
        for (Argument argument : in) {
            final String variableName = argument.getName();
            final VariableInfo var = variableInfo(t, variableName);
            /* No need to add value - it is optional arguments
            final Expression expr = argument.getValueExpr();
            if (expr != null && isValue(expr)) {
                var.value = ((ValueExpression) expr).value;
            }*/
            t.put(variableName, var);
        }
        return super.visit(in, out, t);
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