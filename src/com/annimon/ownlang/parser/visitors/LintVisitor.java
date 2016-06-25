package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.parser.ast.IncludeStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.Visitor;

public abstract class LintVisitor extends AbstractVisitor {

    protected void applyVisitor(IncludeStatement s, Visitor visitor) {
        final Statement program = VisitorUtils.includeProgram(s);
        if (program != null) {
            program.accept(visitor);
        }
    }
}
