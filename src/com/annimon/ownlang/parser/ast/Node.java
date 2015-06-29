package com.annimon.ownlang.parser.ast;

/**
 *
 * @author aNNiMON
 */
public interface Node {
    
    void accept(Visitor visitor);
}
