package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class TernaryExpression implements Node {

    public final Node condition;
    public final Node trueExpr, falseExpr;

    public TernaryExpression(Node condition, Node trueExpr, Node falseExpr) {
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    @Override
    public Value eval() {
        if (condition.eval().asInt() != 0) {
            return trueExpr.eval();
        } else {
            return falseExpr.eval();
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
        return String.format("%s ? %s : %s", condition, trueExpr, falseExpr);
    }
}
