package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.*;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class FunctionalExpression extends InterruptableNode
        implements Statement, SourceLocation {
    
    public final Node functionExpr;
    public final List<Node> arguments;
    private Range range;
    
    public FunctionalExpression(Node functionExpr) {
        this.functionExpr = functionExpr;
        arguments = new ArrayList<>();
    }
    
    public void addArgument(Node arg) {
        arguments.add(arg);
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @Override
    public Range getRange() {
        return range;
    }
    
    @Override
    public Value eval() {
        super.interruptionCheck();
        final int size = arguments.size();
        final Value[] values = new Value[size];
        for (int i = 0; i < size; i++) {
            values[i] = arguments.get(i).eval();
        }
        final Function f = consumeFunction(functionExpr);
        CallStack.enter(functionExpr.toString(), f, range);
        final Value result = f.execute(values);
        CallStack.exit();
        return result;
    }
    
    private Function consumeFunction(Node expr) {
        final Value value = expr.eval();
        if (value.type() == Types.FUNCTION) {
            return ((FunctionValue) value).getValue();
        }
        return getFunction(value.asString());
    }
    
    private Function getFunction(String key) {
        if (ScopeHandler.isFunctionExists(key)) {
            return ScopeHandler.getFunction(key);
        }
        if (ScopeHandler.isVariableOrConstantExists(key)) {
            final Value variable = ScopeHandler.getVariableOrConstant(key);
            if (variable.type() == Types.FUNCTION) {
                return ((FunctionValue)variable).getValue();
            }
        }
        throw new UnknownFunctionException(key, getRange());
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (functionExpr instanceof ValueExpression valueExpr && (valueExpr.value.type() == Types.STRING)) {
            sb.append(valueExpr.value.asString()).append('(');
        } else {
            sb.append(functionExpr).append('(');
        }
        final Iterator<Node> it = arguments.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(", ").append(it.next());
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
