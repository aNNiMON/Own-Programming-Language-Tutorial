package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.parser.ast.*;
import java.util.HashSet;
import java.util.Set;

final class DefaultFunctionsOverrideValidator extends LintVisitor {

    private final Set<String> moduleFunctions = new HashSet<>();

    DefaultFunctionsOverrideValidator(LinterResults results) {
        super(results);
    }

    @Override
    public void visit(FunctionDefineStatement s) {
        super.visit(s);
        if (moduleFunctions.contains(s.name)) {
            results.add(LinterResult.warning(
                    "Function \"%s\" overrides default module function".formatted(s.name)));
        }
    }

    @Override
    public void visit(IncludeStatement s) {
        super.visit(s);
        applyVisitor(s, this);
    }

    @Override
    public void visit(UseStatement s) {
        super.visit(s);
        moduleFunctions.addAll(s.loadFunctions().keySet());
    }
}
