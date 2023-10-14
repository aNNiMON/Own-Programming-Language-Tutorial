package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public interface Node {

    Value eval();
    
    void accept(Visitor visitor);

    <R, T> R accept(ResultVisitor<R, T> visitor, T input);
}
