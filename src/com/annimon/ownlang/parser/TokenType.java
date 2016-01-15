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
    PRINTLN,
    IF,
    ELSE,
    WHILE,
    FOR,
    DO,
    BREAK,
    CONTINUE,
    DEF,
    RETURN,
    USE,
    MATCH,
    CASE,
    
    PLUS, // +
    MINUS, // -
    STAR, // *
    SLASH, // /
    PERCENT,// %
    EQ, // =
    EQEQ, // ==
    EXCL, // !
    EXCLEQ, // !=
    LTEQ, // <=
    LT, // <
    GT, // >
    GTEQ, // >=
    
    LTLT, // <<
    GTGT, // >>
    GTGTGT, // >>>
    
    TILDE, // ~
    CARET, // ^
    BAR, // |
    BARBAR, // ||
    AMP, // &
    AMPAMP, // &&
    
    QUESTION, // ?
    COLON, // :
    COLONCOLON, // ::

    LPAREN, // (
    RPAREN, // )
    LBRACKET, // [
    RBRACKET, // ]
    LBRACE, // {
    RBRACE, // }
    COMMA, // ,
    DOT, // .
    
    EOF
}
