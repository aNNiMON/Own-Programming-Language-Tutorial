package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.parser.ast.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author aNNiMON
 */
final class AssignValidator extends LintVisitor {

    private final Set<String> moduleConstants = new HashSet<>();

    AssignValidator(Collection<LinterResult> results) {
        super(results);
    }

    @Override
    public void visit(AssignmentExpression s) {
        super.visit(s);
        if (s.target instanceof VariableExpression varExpr) {
            final String variable = varExpr.name;
            if (moduleConstants.contains(variable)) {
                results.add(new LinterResult(LinterResult.Severity.WARNING,
                        String.format("Variable \"%s\" overrides constant", variable)));
            }
        }
    }

    @Override
    public void visit(IncludeStatement st) {
        super.visit(st);
        applyVisitor(st, this);
    }

    @Override
    public void visit(UseStatement st) {
        super.visit(st);
        moduleConstants.addAll(st.loadConstants().keySet());
    }
}
