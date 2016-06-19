package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.parser.ast.*;

public final class DefaultFunctionsOverrideValidator extends LintVisitor {

    @Override
    public void visit(FunctionDefineStatement s) {
        super.visit(s);
        if (Functions.isExists(s.name)) {
            Console.error(String.format(
                    "Warning: function \"%s\" overrides default module function", s.name));
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
        st.execute();
    }
}
