package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.Functions;
import static com.annimon.ownlang.lib.Functions.isExists;
import com.annimon.ownlang.lib.UserDefinedFunction;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class FunctionalExpression implements Expression {
    
    public final String name;
    public final List<Expression> arguments;
    
    public FunctionalExpression(String name) {
        this.name = name;
        arguments = new ArrayList<>();
    }
    
    public FunctionalExpression(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
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
        
        final Function function = getFunction(name);
        if (function instanceof UserDefinedFunction) {
            final UserDefinedFunction userFunction = (UserDefinedFunction) function;
            if (size != userFunction.getArgsCount()) throw new RuntimeException("Args count mismatch");
            
            Variables.push();
            for (int i = 0; i < size; i++) {
                Variables.set(userFunction.getArgsName(i), values[i]);
            }
            final Value result = userFunction.execute(values);
            Variables.pop();
            return result;
        }
        return function.execute(values);
    }
    
    private Function getFunction(String key) {
        if (Functions.isExists(key)) return Functions.get(key);
        if (Variables.isExists(key)) {
            final Value value = Variables.get(key);
            if (value instanceof FunctionValue) return ((FunctionValue)value).getValue();
        }
        throw new RuntimeException("Unknown function " + key);
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return name + "(" + arguments.toString() + ")";
    }
}
