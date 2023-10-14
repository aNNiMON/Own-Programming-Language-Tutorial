package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class WhileStatement extends InterruptableNode implements Statement {
    
    public final Node condition;
    public final Statement statement;

    public WhileStatement(Node condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }
    
    @Override
    public Value eval() {
        super.interruptionCheck();
        while (condition.eval().asInt() != 0) {
            try {
                statement.eval();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
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
        return "while " + condition + " " + statement;
    }
}
