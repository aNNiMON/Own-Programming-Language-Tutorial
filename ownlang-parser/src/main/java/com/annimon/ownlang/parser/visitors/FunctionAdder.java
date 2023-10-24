package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.parser.ast.*;

/**
 *
 * @author aNNiMON
 */
public final class FunctionAdder extends AbstractVisitor {

    @Override
    public void visit(FunctionDefineStatement s) {
        super.visit(s);
        s.eval();
    }

    @Override
    public void visit(ClassDeclarationStatement s) {
        // skip, otherwise class methods will be visible outside of class
    }
}
