package com.annimon.ownlang.parser.ast;

/**
 *
 * @author aNNiMON
 */
public final class FunctionStatement implements Statement {
    
    public final FunctionalExpression function;
    
    public FunctionStatement(FunctionalExpression function) {
        this.function = function;
    }

    @Override
    public void execute() {
        function.eval();
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return function.toString();
    }
}
