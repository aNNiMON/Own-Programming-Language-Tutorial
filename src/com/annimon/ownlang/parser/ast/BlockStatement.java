package com.annimon.ownlang.parser.ast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class BlockStatement implements Statement {
    
    private final List<Statement> statements;

    public BlockStatement() {
        statements = new ArrayList<>();
    }
    
    public void add(Statement statement) {
        statements.add(statement);
    }

    @Override
    public void execute() {
        for (Statement statement : statements) {
            statement.execute();
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (Statement statement : statements) {
            result.append(statement.toString()).append(System.lineSeparator());
        }
        return result.toString();
    }
}
