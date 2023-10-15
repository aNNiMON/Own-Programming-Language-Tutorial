package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.parser.ast.IncludeStatement;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.parser.visitors.AbstractVisitor;
import com.annimon.ownlang.parser.visitors.VisitorUtils;

abstract class LintVisitor extends AbstractVisitor {
    protected final LinterResults results;

    LintVisitor(LinterResults results) {
        this.results = results;
    }

    protected void applyVisitor(IncludeStatement s, Visitor visitor) {
        final Node program = VisitorUtils.includeProgram(s);
        if (program != null) {
            program.accept(visitor);
        }
    }
}
