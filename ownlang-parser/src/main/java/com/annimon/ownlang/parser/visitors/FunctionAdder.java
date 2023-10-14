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
}
