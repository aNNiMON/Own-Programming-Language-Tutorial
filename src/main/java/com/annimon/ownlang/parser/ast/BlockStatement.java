package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.Console;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class BlockStatement extends InterruptableNode implements Statement {
    
    public final List<Statement> statements;

    public BlockStatement() {
        statements = new ArrayList<>();
    }
    
    public void add(Statement statement) {
        statements.add(statement);
    }

    @Override
    public void execute() {
        super.interruptionCheck();
        for (Statement statement : statements) {
            statement.execute();
        }
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
        for (Statement statement : statements) {
            result.append(statement.toString()).append(Console.newline());
        }
        return result.toString();
    }
}
