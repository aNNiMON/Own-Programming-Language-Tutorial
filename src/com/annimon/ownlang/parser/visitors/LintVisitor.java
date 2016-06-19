package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.parser.ast.IncludeStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.ValueExpression;
import com.annimon.ownlang.parser.ast.Visitor;
import java.io.IOException;

public abstract class LintVisitor extends AbstractVisitor {

    protected void applyVisitor(IncludeStatement s, Visitor visitor) {
        if (!(s.expression instanceof ValueExpression)) return;
        try {
            final Statement program = s.loadProgram(s.expression.eval().asString());
            program.accept(visitor);
        } catch (IOException ex) {
        }
    }
}
