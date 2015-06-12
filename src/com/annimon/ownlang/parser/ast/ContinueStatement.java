package com.annimon.ownlang.parser.ast;

/**
 *
 * @author aNNiMON
 */
public final class ContinueStatement extends RuntimeException implements Statement {

    @Override
    public void execute() {
        throw this;
    }

    @Override
    public String toString() {
        return "continue";
    }
}
