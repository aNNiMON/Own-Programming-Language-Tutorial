package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.parser.ast.IncludeStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.parser.visitors.AbstractVisitor;
import com.annimon.ownlang.parser.visitors.VisitorUtils;
import java.util.Collection;

abstract class LintVisitor extends AbstractVisitor {
    protected final Collection<LinterResult> results;

    LintVisitor(Collection<LinterResult> results) {
        this.results = results;
    }

    protected void applyVisitor(IncludeStatement s, Visitor visitor) {
        final Statement program = VisitorUtils.includeProgram(s);
        if (program != null) {
            program.accept(visitor);
        }
    }
}
