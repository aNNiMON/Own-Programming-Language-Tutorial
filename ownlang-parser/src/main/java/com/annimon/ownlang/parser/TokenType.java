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
    EXTRACT,
    INCLUDE,
    CLASS,
    NEW,
    
    PLUS, // +
    MINUS, // -
    STAR, // *
    SLASH, // /
    PERCENT,// %
    AT, // @
    
    EQ, // =
    EQEQ, // ==
    EXCL, // !
    EXCLEQ, // !=
    LTEQ, // <=
    LT, // <
    GT, // >
    GTEQ, // >=
    
    PLUSEQ, // +=
    MINUSEQ, // -=
    STAREQ, // *=
    SLASHEQ, // /=
    PERCENTEQ, // %=
    ATEQ, // @=
    AMPEQ, // &=
    CARETEQ, // ^=
    BAREQ, // |=
    COLONCOLONEQ, // ::=
    LTLTEQ, // <<=
    GTGTEQ, // >>=
    GTGTGTEQ, // >>>=
    
    PLUSPLUS, // ++
    MINUSMINUS, // --
    
    LTLT, // <<
    GTGT, // >>
    GTGTGT, // >>>

    DOTDOT, // ..
    STARSTAR, // **
    QUESTIONCOLON, // ?:
    QUESTIONQUESTION, // ??
    
    TILDE, // ~
    CARET, // ^
    CARETCARET, // ^^
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
