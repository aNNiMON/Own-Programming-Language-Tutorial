package com.annimon.ownlang.parser;

/**
 *
 * @author aNNiMON
 */
public enum TokenType {

    NUMBER,
    HEX_NUMBER,
    WORD,
    TEXT,
    
    // keyword
    PRINT,
    
    PLUS,
    MINUS,
    STAR,
    SLASH,
    EQ,
    
    LPAREN, // (
    RPAREN, // )
    
    EOF
}
