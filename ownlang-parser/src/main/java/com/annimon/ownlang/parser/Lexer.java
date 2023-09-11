package com.annimon.ownlang.parser;

import com.annimon.ownlang.exceptions.LexerException;
import java.util.*;

/**
 *
 * @author aNNiMON
 */
public final class Lexer {
    
    public static List<Token> tokenize(String input) {
        return new Lexer(input).tokenize();
    }
    
    private static final String OPERATOR_CHARS = "+-*/%()[]{}=<>!&|.,^~?:";
    
    private static final Map<String, TokenType> OPERATORS;
    static {
        final var operators = new HashMap<String, TokenType>();
        operators.put("+", TokenType.PLUS);
        operators.put("-", TokenType.MINUS);
        operators.put("*", TokenType.STAR);
        operators.put("/", TokenType.SLASH);
        operators.put("%", TokenType.PERCENT);
        operators.put("(", TokenType.LPAREN);
        operators.put(")", TokenType.RPAREN);
        operators.put("[", TokenType.LBRACKET);
        operators.put("]", TokenType.RBRACKET);
        operators.put("{", TokenType.LBRACE);
        operators.put("}", TokenType.RBRACE);
        operators.put("=", TokenType.EQ);
        operators.put("<", TokenType.LT);
        operators.put(">", TokenType.GT);
        operators.put(".", TokenType.DOT);
        operators.put(",", TokenType.COMMA);
        operators.put("^", TokenType.CARET);
        operators.put("~", TokenType.TILDE);
        operators.put("?", TokenType.QUESTION);
        operators.put(":", TokenType.COLON);
        
        operators.put("!", TokenType.EXCL);
        operators.put("&", TokenType.AMP);
        operators.put("|", TokenType.BAR);
        
        operators.put("==", TokenType.EQEQ);
        operators.put("!=", TokenType.EXCLEQ);
        operators.put("<=", TokenType.LTEQ);
        operators.put(">=", TokenType.GTEQ);
        
        operators.put("+=", TokenType.PLUSEQ);
        operators.put("-=", TokenType.MINUSEQ);
        operators.put("*=", TokenType.STAREQ);
        operators.put("/=", TokenType.SLASHEQ);
        operators.put("%=", TokenType.PERCENTEQ);
        operators.put("&=", TokenType.AMPEQ);
        operators.put("^=", TokenType.CARETEQ);
        operators.put("|=", TokenType.BAREQ);
        operators.put("::=", TokenType.COLONCOLONEQ);
        operators.put("<<=", TokenType.LTLTEQ);
        operators.put(">>=", TokenType.GTGTEQ);
        operators.put(">>>=", TokenType.GTGTGTEQ);

        operators.put("++", TokenType.PLUSPLUS);
        operators.put("--", TokenType.MINUSMINUS);
        
        operators.put("::", TokenType.COLONCOLON);
        
        operators.put("&&", TokenType.AMPAMP);
        operators.put("||", TokenType.BARBAR);
        
        operators.put("<<", TokenType.LTLT);
        operators.put(">>", TokenType.GTGT);
        operators.put(">>>", TokenType.GTGTGT);

        operators.put("@", TokenType.AT);
        operators.put("@=", TokenType.ATEQ);
        operators.put("..", TokenType.DOTDOT);
        operators.put("**", TokenType.STARSTAR);
        operators.put("^^", TokenType.CARETCARET);
        operators.put("?:", TokenType.QUESTIONCOLON);
        operators.put("??", TokenType.QUESTIONQUESTION);
        OPERATORS = Map.copyOf(operators);
    }
    
    private static final Map<String, TokenType> KEYWORDS;
    static {
        final var keywords = new HashMap<String, TokenType>();
        keywords.put("print", TokenType.PRINT);
        keywords.put("println", TokenType.PRINTLN);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("for", TokenType.FOR);
        keywords.put("do", TokenType.DO);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("def", TokenType.DEF);
        keywords.put("return", TokenType.RETURN);
        keywords.put("use", TokenType.USE);
        keywords.put("match", TokenType.MATCH);
        keywords.put("case", TokenType.CASE);
        keywords.put("extract", TokenType.EXTRACT);
        keywords.put("include", TokenType.INCLUDE);
        keywords.put("class", TokenType.CLASS);
        keywords.put("new", TokenType.NEW);
        KEYWORDS = Map.copyOf(keywords);
    }

    public static Set<String> getKeywords() {
        return KEYWORDS.keySet();
    }

    private final String input;
    private final int length;
    
    private final List<Token> tokens;
    private final StringBuilder buffer;
    
    private int pos;
    private int row, col;

    public Lexer(String input) {
        this.input = input;
        length = input.length();
        
        tokens = new ArrayList<>();
        buffer = new StringBuilder();
        row = col = 1;
    }
    
    public List<Token> tokenize() {
        while (pos < length) {
            // Fast path for skipping whitespaces
            while (Character.isWhitespace(peek(0))) {
                next();
            }

            final char current = peek(0);
            if (Character.isDigit(current)) tokenizeNumber();
            else if (isOwnLangIdentifierStart(current)) tokenizeWord();
            else if (current == '`') tokenizeExtendedWord();
            else if (current == '"') tokenizeText();
            else if (current == '#') tokenizeHexNumber(1);
            else if (OPERATOR_CHARS.indexOf(current) != -1) {
                tokenizeOperator();
            } else {
                // whitespaces
                next();
            }
        }
        return tokens;
    }
    
    private void tokenizeNumber() {
        clearBuffer();
        final Pos startPos = markPos();
        char current = peek(0);
        if (current == '0' && (peek(1) == 'x' || (peek(1) == 'X'))) {
            tokenizeHexNumber(2);
            return;
        }
        boolean hasDot = false;
        while (true) {
            if (current == '.') {
                if (hasDot) throw error("Invalid float number");
                hasDot = true;
            } else if (!Character.isDigit(current)) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.NUMBER, buffer.toString(), startPos);
    }
    
    private void tokenizeHexNumber(int skipChars) {
        clearBuffer();
        final Pos startPos = markPos();
        // Skip HEX prefix 0x or #
        for (int i = 0; i < skipChars; i++) next();

        char current = peek(0);
        while (isHexNumber(current) || (current == '_')) {
            if (current != '_') {
                // allow _ symbol
                buffer.append(current);
            }
            current = next();
        }
        if (!buffer.isEmpty()) {
            addToken(TokenType.HEX_NUMBER, buffer.toString(), startPos);
        }
    }

    private static boolean isHexNumber(char current) {
        return Character.isDigit(current)
                || ('a' <= current && current <= 'f')
                || ('A' <= current && current <= 'F');
    }
    
    private void tokenizeOperator() {
        char current = peek(0);
        if (current == '/') {
            if (peek(1) == '/') {
                next();
                next();
                tokenizeComment();
                return;
            } else if (peek(1) == '*') {
                next();
                next();
                tokenizeMultilineComment();
                return;
            }
        }

        final Pos startPos = markPos();
        clearBuffer();
        while (true) {
            if (!buffer.isEmpty() && !OPERATORS.containsKey(buffer.toString() + current)) {
                addToken(OPERATORS.get(buffer.toString()), startPos);
                return;
            }
            buffer.append(current);
            current = next();
        }
    }
    
    private void tokenizeWord() {
        clearBuffer();
        final Pos startPos = markPos();
        buffer.append(peek(0));
        char current = next();
        while (isOwnLangIdentifierPart(current)) {
            buffer.append(current);
            current = next();
        }
        
        final String word = buffer.toString();
        if (KEYWORDS.containsKey(word)) {
            addToken(KEYWORDS.get(word), startPos);
        } else {
            addToken(TokenType.WORD, word, startPos);
        }
    }

    private void tokenizeExtendedWord() {
        final Pos startPos = markPos();
        next();// skip `
        clearBuffer();
        char current = peek(0);
        while (current != '`') {
            if (current == '\0') throw error("Reached end of file while parsing extended word.");
            if (current == '\n' || current == '\r') throw error("Reached end of line while parsing extended word.");
            buffer.append(current);
            current = next();
        }
        next(); // skip closing `
        addToken(TokenType.WORD, buffer.toString(), startPos);
    }
    
    private void tokenizeText() {
        final Pos startPos = markPos();
        next();// skip "
        clearBuffer();
        char current = peek(0);
        while (true) {
            if (current == '\\') {
                current = next();
                switch (current) {
                    case '"': current = next(); buffer.append('"'); continue;
                    case '0': current = next(); buffer.append('\0'); continue;
                    case 'b': current = next(); buffer.append('\b'); continue;
                    case 'f': current = next(); buffer.append('\f'); continue;
                    case 'n': current = next(); buffer.append('\n'); continue;
                    case 'r': current = next(); buffer.append('\r'); continue;
                    case 't': current = next(); buffer.append('\t'); continue;
                    case 'u': // http://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.3
                        int rollbackPosition = pos;
                        while (current == 'u') current = next();
                        int escapedValue = 0;
                        for (int i = 12; i >= 0 && escapedValue != -1; i -= 4) {
                            if (isHexNumber(current)) {
                                escapedValue |= (Character.digit(current, 16) << i);
                            } else {
                                escapedValue = -1;
                            }
                            current = next();
                        }
                        if (escapedValue >= 0) {
                            buffer.append((char) escapedValue);
                        } else {
                            // rollback
                            buffer.append("\\u");
                            pos = rollbackPosition;
                        }
                        continue;
                }
                buffer.append('\\');
                continue;
            }
            if (current == '"') break;
            if (current == '\0') throw error("Reached end of file while parsing text string.");
            buffer.append(current);
            current = next();
        }
        next(); // skip closing "
        
        addToken(TokenType.TEXT, buffer.toString(), startPos);
    }
    
    private void tokenizeComment() {
        char current = peek(0);
        while ("\r\n\0".indexOf(current) == -1) {
            current = next();
        }
     }
    
    private void tokenizeMultilineComment() {
        char current = peek(0);
        while (current != '*' || peek(1) != '/') {
            if (current == '\0') throw error("Reached end of file while parsing multiline comment");
            current = next();
        }
        next(); // *
        next(); // /
    }

    private boolean isOwnLangIdentifierStart(char current) {
        return (Character.isLetter(current) || (current == '_') || (current == '$'));
    }

    private boolean isOwnLangIdentifierPart(char current) {
        return (Character.isLetterOrDigit(current) || (current == '_') || (current == '$'));
    }
    
    private void clearBuffer() {
        buffer.setLength(0);
    }

    private Pos markPos() {
        return new Pos(row, col);
    }
    
    private char next() {
        final char result = peek(0);
        if (result == '\n') {
            row++;
            col = 1;
        } else col++;

        pos++;
        return peek(0);
    }
    
    private char peek(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= length) return '\0';
        return input.charAt(position);
    }
    
    private void addToken(TokenType type, Pos startPos) {
        addToken(type, "", startPos);
    }
    
    private void addToken(TokenType type, String text, Pos startRow) {
        tokens.add(new Token(type, text, startRow));
    }

    private LexerException error(String text) {
        return new LexerException(markPos(), text);
    }
}
