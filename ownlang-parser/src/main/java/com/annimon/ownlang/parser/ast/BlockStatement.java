package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class BlockStatement extends InterruptableNode implements Statement {
    
    public final List<Node> statements;

    public BlockStatement() {
        statements = new ArrayList<>();
    }
    
    public void add(Node statement) {
        statements.add(statement);
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        for (Node statement : statements) {
            statement.eval();
        }
        return NumberValue.ZERO;
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
        final StringBuilder result = new StringBuilder();
        for (Node statement : statements) {
            result.append(statement.toString()).append(Console.newline());
        }
        return result.toString();
    }
}
