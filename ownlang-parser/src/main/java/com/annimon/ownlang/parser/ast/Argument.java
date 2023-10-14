package com.annimon.ownlang.parser.ast;

public record Argument(String name, Node valueExpr) {

    public Argument(String name) {
        this(name, null);
    }

    @Override
    public String toString() {
        return name + (valueExpr == null ? "" : " = " + valueExpr);
    }
}
