package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.ast.*;
import java.util.HashMap;
import java.util.Map;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isValue;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isVariable;

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
        if (!isVariable(s.target)) {
            return super.visit(s, t);
        }

        final String variableName = ((VariableExpression) s.target).name;
        final VariableInfo var = grabVariableInfo(t, variableName);

        if (s.operation == null && isValue(s.expression)) {
            var.value = ((ValueExpression) s.expression).value;
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(DestructuringAssignmentStatement s, Map<String, VariableInfo> t) {
        for (String variableName : s.variables) {
            if (variableName == null) continue;
            grabVariableInfo(t, variableName);
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(ForeachArrayStatement s, Map<String, VariableInfo> t) {
        grabVariableInfo(t, s.variable);
        return super.visit(s, t);
    }

    @Override
    public Node visit(ForeachMapStatement s, Map<String, VariableInfo> t) {
        grabVariableInfo(t, s.key);
        grabVariableInfo(t, s.value);
        return super.visit(s, t);
    }

    @Override
    public Node visit(MatchExpression s, Map<String, VariableInfo> t) {
        for (MatchExpression.Pattern pattern : s.patterns) {
            if (pattern instanceof MatchExpression.VariablePattern varPattern) {
                final String variableName = varPattern.variable;
                grabVariableInfo(t, variableName);
            }
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(UnaryExpression s, Map<String, VariableInfo> t) {
        if (s.expr1 instanceof Accessible) {
            if (s.expr1 instanceof VariableExpression varExpr) {
                grabVariableInfo(t, varExpr.name);
            }
            if (s.expr1 instanceof ContainerAccessExpression conExpr) {
                if (conExpr.rootIsVariable()) {
                    final String variableName = ((VariableExpression) conExpr.root).name;
                    grabVariableInfo(t, variableName);
                }
            }
        }
        return super.visit(s, t);
    }

    @Override
    public Node visit(UseStatement s, Map<String, VariableInfo> t) {
        if (grabModuleConstants) {
            for (Map.Entry<String, Value> entry : s.loadConstants().entrySet()) {
                final VariableInfo var = grabVariableInfo(t, entry.getKey());
                var.value = entry.getValue();
            }
        }
        return super.visit(s, t);
    }

    @Override
    protected boolean visit(Arguments in, Arguments out, Map<String, VariableInfo> t) {
        for (Argument argument : in) {
            final String variableName = argument.name();
            grabVariableInfo(t, variableName);
            /* No need to add value - it is optional arguments
            final Node expr = argument.getValueExpr();
            if (expr != null && isValue(expr)) {
                var.value = ((ValueExpression) expr).value;
            }*/
        }
        return super.visit(in, out, t);
    }



    private VariableInfo grabVariableInfo(Map<String, VariableInfo> t, final String variableName) {
        final VariableInfo var;
        if (t.containsKey(variableName)) {
            var = t.get(variableName);
            var.modifications++;
        } else {
            var = new VariableInfo();
            var.modifications = 1;
            t.put(variableName, var);
        }
        return var;
    }
}
