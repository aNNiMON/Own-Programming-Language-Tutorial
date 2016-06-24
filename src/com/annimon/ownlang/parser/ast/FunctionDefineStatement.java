package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.UserDefinedFunction;

/**
 *
 * @author aNNiMON
 */
public final class FunctionDefineStatement implements Statement {
    
    public final String name;
    public final Arguments arguments;
    public final Statement body;
    
    public FunctionDefineStatement(String name, Arguments arguments, Statement body) {
        this.name = name;
        this.arguments = arguments;
        this.body = body;
    }

    @Override
    public void execute() {
        Functions.set(name, new UserDefinedFunction(arguments, body));
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
        if (body instanceof ReturnStatement) {
            return String.format("def %s%s = %s", name, arguments, ((ReturnStatement)body).expression);
        }
        return String.format("def %s%s %s", name, arguments, body);
    }
}
