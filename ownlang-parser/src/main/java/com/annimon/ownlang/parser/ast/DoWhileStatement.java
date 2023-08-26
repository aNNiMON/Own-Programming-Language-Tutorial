package com.annimon.ownlang.parser.ast;

/**
 *
 * @author aNNiMON
 */
public final class DoWhileStatement extends InterruptableNode implements Statement {
    
    public final Expression condition;
    public final Statement statement;

    public DoWhileStatement(Expression condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }
    
    @Override
    public void execute() {
        super.interruptionCheck();
        do {
            try {
                statement.execute();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
        }
        while (condition.eval().asInt() != 0);
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
        return "do " + statement + " while " + condition;
    }
}
