package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.VariableDoesNotExistsException;
import com.annimon.ownlang.exceptions.UnknownFunctionException;
import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class FunctionalExpression implements Expression {
    
    public final Expression functionExpr;
    public final List<Expression> arguments;
    
    public FunctionalExpression(Expression functionExpr) {
        this.functionExpr = functionExpr;
        arguments = new ArrayList<>();
    }
    
    public void addArgument(Expression arg) {
        arguments.add(arg);
    }
    
    @Override
    public Value eval() {
        final int size = arguments.size();
        final Value[] values = new Value[size];
        for (int i = 0; i < size; i++) {
            values[i] = arguments.get(i).eval();
        }
        return consumeFunction(functionExpr).execute(values);
    }
    
    private Function consumeFunction(Expression expr) {
        try {
            final Value value = expr.eval();
            if (value.type() == Types.FUNCTION) {
                return ((FunctionValue) value).getValue();
            }
            return getFunction(value.asString());
        } catch (VariableDoesNotExistsException ex) {
            return getFunction(ex.getVariable());
        }
    }
    
    private Function getFunction(String key) {
        if (Functions.isExists(key)) return Functions.get(key);
        if (Variables.isExists(key)) {
            final Value variable = Variables.get(key);
            if (variable.type() == Types.FUNCTION) {
                return ((FunctionValue)variable).getValue();
            }
        }
        throw new UnknownFunctionException(key);
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return functionExpr + "(" + arguments.toString() + ")";
    }
}
