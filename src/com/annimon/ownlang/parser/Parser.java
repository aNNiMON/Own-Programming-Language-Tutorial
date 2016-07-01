package com.annimon.ownlang.parser;

import com.annimon.ownlang.exceptions.ParseException;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.UserDefinedFunction;
import com.annimon.ownlang.parser.ast.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class Parser {
    
    public static Statement parse(List<Token> tokens) {
        final Parser parser = new Parser(tokens);
        final Statement program = parser.parse();
        if (parser.getParseErrors().hasErrors()) {
            throw new ParseException();
        }
        return program;
    }
    
    private static final Token EOF = new Token(TokenType.EOF, "", -1, -1);

    private static final EnumMap<TokenType, BinaryExpression.Operator> assignOperator;
    static {
        assignOperator = new EnumMap(TokenType.class);
        assignOperator.put(TokenType.EQ, null);
        assignOperator.put(TokenType.PLUSEQ, BinaryExpression.Operator.ADD);
        assignOperator.put(TokenType.MINUSEQ, BinaryExpression.Operator.SUBTRACT);
        assignOperator.put(TokenType.STAREQ, BinaryExpression.Operator.MULTIPLY);
        assignOperator.put(TokenType.SLASHEQ, BinaryExpression.Operator.DIVIDE);
        assignOperator.put(TokenType.PERCENTEQ, BinaryExpression.Operator.REMAINDER);
        assignOperator.put(TokenType.AMPEQ, BinaryExpression.Operator.AND);
        assignOperator.put(TokenType.CARETEQ, BinaryExpression.Operator.XOR);
        assignOperator.put(TokenType.BAREQ, BinaryExpression.Operator.OR);
        assignOperator.put(TokenType.COLONCOLONEQ, BinaryExpression.Operator.PUSH);
        assignOperator.put(TokenType.LTLTEQ, BinaryExpression.Operator.LSHIFT);
        assignOperator.put(TokenType.GTGTEQ, BinaryExpression.Operator.RSHIFT);
        assignOperator.put(TokenType.GTGTGTEQ, BinaryExpression.Operator.URSHIFT);
        assignOperator.put(TokenType.ATEQ, BinaryExpression.Operator.AT);
    }

    private final List<Token> tokens;
    private final int size;
    private final ParseErrors parseErrors;
    private Statement parsedStatement;
    
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        size = tokens.size();
        parseErrors = new ParseErrors();
    }
    
    public Statement getParsedStatement() {
        return parsedStatement;
    }
    
    public ParseErrors getParseErrors() {
        return parseErrors;
    }
    
    public Statement parse() {
        parseErrors.clear();
        final BlockStatement result = new BlockStatement();
        while (!match(TokenType.EOF)) {
            try {
                result.add(statement());
            } catch (Exception ex) {
                parseErrors.add(ex, getErrorLine());
                recover();
            }
        }
        parsedStatement = result;
        return result;
    }
    
    private int getErrorLine() {
        if (size == 0) return 0;
        if (pos >= size) return tokens.get(size - 1).getRow();
        return tokens.get(pos).getRow();
    }
    
    private void recover() {
        int preRecoverPosition = pos;
        for (int i = preRecoverPosition; i <= size; i++) {
            pos = i;
            try {
                statement();
                // successfully parsed,
                pos = i; // restore position
                return;
            } catch (Exception ex) {
                // fail
            }
        }
    }
    
    private Statement block() {
        final BlockStatement block = new BlockStatement();
        consume(TokenType.LBRACE);
        while (!match(TokenType.RBRACE)) {
            block.add(statement());
        }
        return block;
    }
    
    private Statement statementOrBlock() {
        if (lookMatch(0, TokenType.LBRACE)) return block();
        return statement();
    }
    
    private Statement statement() {
        if (match(TokenType.PRINT)) {
            return new PrintStatement(expression());
        }
        if (match(TokenType.PRINTLN)) {
            return new PrintlnStatement(expression());
        }
        if (match(TokenType.IF)) {
            return ifElse();
        }
        if (match(TokenType.WHILE)) {
            return whileStatement();
        }
        if (match(TokenType.DO)) {
            return doWhileStatement();
        }
        if (match(TokenType.BREAK)) {
            return new BreakStatement();
        }
        if (match(TokenType.CONTINUE)) {
            return new ContinueStatement();
        }
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(expression());
        }
        if (match(TokenType.USE)) {
            return new UseStatement(expression());
        }
        if (match(TokenType.INCLUDE)) {
            return new IncludeStatement(expression());
        }
        if (match(TokenType.FOR)) {
            return forStatement();
        }
        if (match(TokenType.DEF)) {
            return functionDefine();
        }
        if (match(TokenType.MATCH)) {
            return new ExprStatement(match());
        }
        if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN)) {
            return new ExprStatement(function(qualifiedName()));
        }
        return assignmentStatement();
    }
    
    private Statement assignmentStatement() {
        if (match(TokenType.EXTRACT)) {
            return destructuringAssignment();
        }
        final Expression expression = expression();
        if (expression instanceof Statement) {
            return (Statement) expression;
        }
        throw new ParseException("Unknown statement: " + get(0));
    }
    
    private DestructuringAssignmentStatement destructuringAssignment() {
        // extract(var1, var2, ...) = ...
        consume(TokenType.LPAREN);
        final List<String> variables = new ArrayList<>();
        while (!match(TokenType.RPAREN)) {
            if (lookMatch(0, TokenType.WORD)) {
                variables.add(consume(TokenType.WORD).getText());
            } else {
                variables.add(null);
            }
            match(TokenType.COMMA);
        }
        consume(TokenType.EQ);
        return new DestructuringAssignmentStatement(variables, expression());
    }
    
    private Statement ifElse() {
        final Expression condition = expression();
        final Statement ifStatement = statementOrBlock();
        final Statement elseStatement;
        if (match(TokenType.ELSE)) {
            elseStatement = statementOrBlock();
        } else {
            elseStatement = null;
        }
         return new IfStatement(condition, ifStatement, elseStatement);
    }
    
    private Statement whileStatement() {
        final Expression condition = expression();
        final Statement statement = statementOrBlock();
        return new WhileStatement(condition, statement);
    }
    
    private Statement doWhileStatement() {
        final Statement statement = statementOrBlock();
        consume(TokenType.WHILE);
        final Expression condition = expression();
        return new DoWhileStatement(condition, statement);
    }
    
    private Statement forStatement() {
        int foreachIndex = lookMatch(0, TokenType.LPAREN) ? 1 : 0;
        if (lookMatch(foreachIndex, TokenType.WORD) && lookMatch(foreachIndex + 1, TokenType.COLON)) {
            // for v : arr || for (v : arr)
            return foreachArrayStatement();
        }
        if (lookMatch(foreachIndex, TokenType.WORD) && lookMatch(foreachIndex + 1, TokenType.COMMA)
                && lookMatch(foreachIndex + 2, TokenType.WORD) && lookMatch(foreachIndex + 3, TokenType.COLON)) {
            // for key, value : arr || for (key, value : arr)
            return foreachMapStatement();
        }
        
        boolean openParen = match(TokenType.LPAREN); // необязательные скобки
        final Statement initialization = assignmentStatement();
        consume(TokenType.COMMA);
        final Expression termination = expression();
        consume(TokenType.COMMA);
        final Statement increment = assignmentStatement();
        if (openParen) consume(TokenType.RPAREN); // скобки
        final Statement statement = statementOrBlock();
        return new ForStatement(initialization, termination, increment, statement);
    }
    
    private ForeachArrayStatement foreachArrayStatement() {
        boolean openParen = match(TokenType.LPAREN); // необязательные скобки
        final String variable = consume(TokenType.WORD).getText();
        consume(TokenType.COLON);
        final Expression container = expression();
        if (openParen) consume(TokenType.RPAREN); // скобки
        final Statement statement = statementOrBlock();
        return new ForeachArrayStatement(variable, container, statement);
    }
    
    private ForeachMapStatement foreachMapStatement() {
        boolean openParen = match(TokenType.LPAREN); // необязательные скобки
        final String key = consume(TokenType.WORD).getText();
        consume(TokenType.COMMA);
        final String value = consume(TokenType.WORD).getText();
        consume(TokenType.COLON);
        final Expression container = expression();
        if (openParen) consume(TokenType.RPAREN); // скобки
        final Statement statement = statementOrBlock();
        return new ForeachMapStatement(key, value, container, statement);
    }
    
    private FunctionDefineStatement functionDefine() {
        // def name(arg1, arg2 = value) { ... }  ||  def name(args) = expr
        final String name = consume(TokenType.WORD).getText();
        final Arguments arguments = arguments();
        final Statement body = statementBody();
        return new FunctionDefineStatement(name, arguments, body);
    }
    
    private Arguments arguments() {
        // (arg1, arg2, arg3 = expr1, arg4 = expr2)
        final Arguments arguments = new Arguments();
        boolean startsOptionalArgs = false;
        consume(TokenType.LPAREN);
        while (!match(TokenType.RPAREN)) {
            final String name = consume(TokenType.WORD).getText();
            if (match(TokenType.EQ)) {
                startsOptionalArgs = true;
                arguments.addOptional(name, variable());
            } else if (!startsOptionalArgs) {
                arguments.addRequired(name);
            } else {
                throw new ParseException("Required argument cannot be after optional");
            }
            match(TokenType.COMMA);
        }
        return arguments;
    }
    
    private Statement statementBody() {
        if (match(TokenType.EQ)) {
            return new ReturnStatement(expression());
        }
        return statementOrBlock();
    }
    
    private FunctionalExpression function(Expression qualifiedNameExpr) {
        // function(arg1, arg2, ...)
        consume(TokenType.LPAREN);
        final FunctionalExpression function = new FunctionalExpression(qualifiedNameExpr);
        while (!match(TokenType.RPAREN)) {
            function.addArgument(expression());
            match(TokenType.COMMA);
        }
        return function;
    }
    
    private Expression array() {
        // [value1, value2, ...]
        consume(TokenType.LBRACKET);
        final List<Expression> elements = new ArrayList<>();
        while (!match(TokenType.RBRACKET)) {
            elements.add(expression());
            match(TokenType.COMMA);
        }
        return new ArrayExpression(elements);
    }
    
    private Expression map() {
        // {key1 : value1, key2 : value2, ...}
        consume(TokenType.LBRACE);
        final Map<Expression, Expression> elements = new HashMap<>();
        while (!match(TokenType.RBRACE)) {
            final Expression key = primary();
            consume(TokenType.COLON);
            final Expression value = expression();
            elements.put(key, value);
            match(TokenType.COMMA);
        }
        return new MapExpression(elements);
    }
    
    private MatchExpression match() {
        // match expression {
        //  case pattern1: result1
        //  case pattern2 if extr: result2
        // }
        final Expression expression = expression();
        consume(TokenType.LBRACE);
        final List<MatchExpression.Pattern> patterns = new ArrayList<>();
        do {
            consume(TokenType.CASE);
            MatchExpression.Pattern pattern = null;
            final Token current = get(0);
            if (match(TokenType.NUMBER)) {
                // case 0.5: 
                pattern = new MatchExpression.ConstantPattern(
                        NumberValue.of(createNumber(current.getText(), 10))
                );
            } else if (match(TokenType.HEX_NUMBER)) {
                // case #FF: 
                pattern = new MatchExpression.ConstantPattern(
                        NumberValue.of(createNumber(current.getText(), 16))
                );
            } else if (match(TokenType.TEXT)) {
                // case "text":
                pattern = new MatchExpression.ConstantPattern(
                        new StringValue(current.getText())
                );
            } else if (match(TokenType.WORD)) {
                // case value: 
                pattern = new MatchExpression.VariablePattern(current.getText());
            } else if (match(TokenType.LBRACKET)) {
                // case [x :: xs]:
                final MatchExpression.ListPattern listPattern = new MatchExpression.ListPattern();
                while (!match(TokenType.RBRACKET)) {
                    listPattern.add(consume(TokenType.WORD).getText());
                    match(TokenType.COLONCOLON);
                }
                pattern = listPattern;
            } else if (match(TokenType.LPAREN)) {
                // case (1, 2):
                final MatchExpression.TuplePattern tuplePattern = new MatchExpression.TuplePattern();
                while (!match(TokenType.RPAREN)) {
                    if ("_".equals(get(0).getText())) {
                        tuplePattern.addAny();
                        consume(TokenType.WORD);
                    } else {
                        tuplePattern.add(expression());
                    }
                    match(TokenType.COMMA);
                }
                pattern = tuplePattern;
            }
            
            if (pattern == null) {
                throw new ParseException("Wrong pattern in match expression: " + current);
            }
            if (match(TokenType.IF)) {
                // case e if e > 0:
                pattern.optCondition = expression();
            }
            
            consume(TokenType.COLON);
            if (lookMatch(0, TokenType.LBRACE)) {
                pattern.result = block();
            } else {
                pattern.result = new ReturnStatement(expression());
            }
            patterns.add(pattern);
        } while (!match(TokenType.RBRACE));
        
        return new MatchExpression(expression, patterns);
    }
    
    private Expression expression() {
        return assignment();
    }
    
    private Expression assignment() {
        final Expression assignment = assignmentStrict();
        if (assignment != null) {
            return assignment;
        }
        return ternary();
    }
    
    private Expression assignmentStrict() {
        final int position = pos;
        final Expression targetExpr = qualifiedName();
        if ((targetExpr == null) || !(targetExpr instanceof Accessible)) {
            pos = position;
            return null;
        }
        
        final TokenType currentType = get(0).getType();
        if (!assignOperator.containsKey(currentType)) {
            pos = position;
            return null;
        }
        match(currentType);
        
        final BinaryExpression.Operator op = assignOperator.get(currentType);
        final Expression expression = expression();
        
        return new AssignmentExpression(op, (Accessible) targetExpr, expression);
    }
    
    private Expression ternary() {
        Expression result = logicalOr();
        
        if (match(TokenType.QUESTION)) {
            final Expression trueExpr = expression();
            consume(TokenType.COLON);
            final Expression falseExpr = expression();
            return new TernaryExpression(result, trueExpr, falseExpr);
        }
        if (match(TokenType.QUESTIONCOLON)) {
            return new BinaryExpression(BinaryExpression.Operator.ELVIS, result, expression());
        }
        return result;
    }
    
    private Expression logicalOr() {
        Expression result = logicalAnd();
        
        while (true) {
            if (match(TokenType.BARBAR)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.OR, result, logicalAnd());
                continue;
            }
            break;
        }
        
        return result;
    }
    
    private Expression logicalAnd() {
        Expression result = bitwiseOr();
        
        while (true) {
            if (match(TokenType.AMPAMP)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.AND, result, bitwiseOr());
                continue;
            }
            break;
        }
        
        return result;
    }
    
    private Expression bitwiseOr() {
        Expression expression = bitwiseXor();
        
        while (true) {
            if (match(TokenType.BAR)) {
                expression = new BinaryExpression(BinaryExpression.Operator.OR, expression, bitwiseXor());
                continue;
            }
            break;
        }
        
        return expression;
    }
    
    private Expression bitwiseXor() {
        Expression expression = bitwiseAnd();
        
        while (true) {
            if (match(TokenType.CARET)) {
                expression = new BinaryExpression(BinaryExpression.Operator.XOR, expression, bitwiseAnd());
                continue;
            }
            break;
        }
        
        return expression;
    }
    
    private Expression bitwiseAnd() {
        Expression expression = equality();
        
        while (true) {
            if (match(TokenType.AMP)) {
                expression = new BinaryExpression(BinaryExpression.Operator.AND, expression, equality());
                continue;
            }
            break;
        }
        
        return expression;
    }
    
    private Expression equality() {
        Expression result = conditional();
        
        if (match(TokenType.EQEQ)) {
            return new ConditionalExpression(ConditionalExpression.Operator.EQUALS, result, conditional());
        }
        if (match(TokenType.EXCLEQ)) {
            return new ConditionalExpression(ConditionalExpression.Operator.NOT_EQUALS, result, conditional());
        }
        
        return result;
    }
    
    private Expression conditional() {
        Expression result = shift();
        
        while (true) {
            if (match(TokenType.LT)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.LT, result, shift());
                continue;
            }
            if (match(TokenType.LTEQ)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.LTEQ, result, shift());
                continue;
            }
            if (match(TokenType.GT)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.GT, result, shift());
                continue;
            }
            if (match(TokenType.GTEQ)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.GTEQ, result, shift());
                continue;
            }
            break;
        }
        
        return result;
    }
    
    private Expression shift() {
        Expression expression = additive();
        
        while (true) {
            if (match(TokenType.LTLT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.LSHIFT, expression, additive());
                continue;
            }
            if (match(TokenType.GTGT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.RSHIFT, expression, additive());
                continue;
            }
            if (match(TokenType.GTGTGT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.URSHIFT, expression, additive());
                continue;
            }
            if (match(TokenType.DOTDOT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.RANGE, expression, additive());
                continue;
            }
            break;
        }
        
        return expression;
    }
    
    private Expression additive() {
        Expression result = multiplicative();
        
        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryExpression(BinaryExpression.Operator.ADD, result, multiplicative());
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression(BinaryExpression.Operator.SUBTRACT, result, multiplicative());
                continue;
            }
            if (match(TokenType.COLONCOLON)) {
                result = new BinaryExpression(BinaryExpression.Operator.PUSH, result, multiplicative());
                continue;
            }
            if (match(TokenType.AT)) {
                result = new BinaryExpression(BinaryExpression.Operator.AT, result, multiplicative());
                continue;
            }
            break;
        }
        
        return result;
    }
    
    private Expression multiplicative() {
        Expression result = unary();
        
        while (true) {
            if (match(TokenType.STAR)) {
                result = new BinaryExpression(BinaryExpression.Operator.MULTIPLY, result, unary());
                continue;
            }
            if (match(TokenType.SLASH)) {
                result = new BinaryExpression(BinaryExpression.Operator.DIVIDE, result, unary());
                continue;
            }
            if (match(TokenType.PERCENT)) {
                result = new BinaryExpression(BinaryExpression.Operator.REMAINDER, result, unary());
                continue;
            }
            if (match(TokenType.STARSTAR)) {
                result = new BinaryExpression(BinaryExpression.Operator.POWER, result, unary());
                continue;
            }
            break;
        }
        
        return result;
    }
    
    private Expression unary() {
        if (match(TokenType.PLUSPLUS)) {
            return new UnaryExpression(UnaryExpression.Operator.INCREMENT_PREFIX, primary());
        }
        if (match(TokenType.MINUSMINUS)) {
            return new UnaryExpression(UnaryExpression.Operator.DECREMENT_PREFIX, primary());
        }
        if (match(TokenType.MINUS)) {
            return new UnaryExpression(UnaryExpression.Operator.NEGATE, primary());
        }
        if (match(TokenType.EXCL)) {
            return new UnaryExpression(UnaryExpression.Operator.NOT, primary());
        }
        if (match(TokenType.TILDE)) {
            return new UnaryExpression(UnaryExpression.Operator.COMPLEMENT, primary());
        }
        if (match(TokenType.PLUS)) {
            return primary();
        }
        return primary();
    }
    
    private Expression primary() {
        if (match(TokenType.LPAREN)) {
            Expression result = expression();
            match(TokenType.RPAREN);
            return result;
        }
        
        if (match(TokenType.COLONCOLON)) {
            final String functionName = consume(TokenType.WORD).getText();
            return new FunctionReferenceExpression(functionName);
        }
        if (match(TokenType.MATCH)) {
            return match();
        }
        if (match(TokenType.DEF)) {
            final Arguments arguments = arguments();
            final Statement statement = statementBody();
            return new ValueExpression(new UserDefinedFunction(arguments, statement));
        }
        return variable();
    }
    
    private Expression variable() {
        // function(...
        if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN)) {
            return function(new ValueExpression(consume(TokenType.WORD).getText()));
        }
        
        final Expression qualifiedNameExpr = qualifiedName();
        if (qualifiedNameExpr != null) {
            // variable(args) || arr["key"](args) || obj.key(args)
            if (lookMatch(0, TokenType.LPAREN)) {
                return function(qualifiedNameExpr);
            }
            // postfix increment/decrement
            if (match(TokenType.PLUSPLUS)) {
                return new UnaryExpression(UnaryExpression.Operator.INCREMENT_POSTFIX, qualifiedNameExpr);
            }
            if (match(TokenType.MINUSMINUS)) {
                return new UnaryExpression(UnaryExpression.Operator.DECREMENT_POSTFIX, qualifiedNameExpr);
            }
            return qualifiedNameExpr;
        }
        
        if (lookMatch(0, TokenType.LBRACKET)) {
            return array();
        }
        if (lookMatch(0, TokenType.LBRACE)) {
            return map();
        }
        return value();
    }
    
    private Expression qualifiedName() {
        // var || var.key[index].key2
        final Token current = get(0);
        if (!match(TokenType.WORD)) return null;
        
        final List<Expression> indices = variableSuffix();
        if ((indices == null) || indices.isEmpty()) {
            return new VariableExpression(current.getText());
        }
        return new ContainerAccessExpression(current.getText(), indices);
    }
    
    private List<Expression> variableSuffix() {
        // .key1.arr1[expr1][expr2].key2
        if (!lookMatch(0, TokenType.DOT) && !lookMatch(0, TokenType.LBRACKET)) {
            return null;
        }
        final List<Expression> indices = new ArrayList<>();
        while (lookMatch(0, TokenType.DOT) || lookMatch(0, TokenType.LBRACKET)) {
            if (match(TokenType.DOT)) {
                final String fieldName = consume(TokenType.WORD).getText();
                final Expression key = new ValueExpression(fieldName);
                indices.add(key);
            }
            if (match(TokenType.LBRACKET)) {
                indices.add(expression());
                consume(TokenType.RBRACKET);
            }
        }
        return indices;
    }
    
    private Expression value() {
        final Token current = get(0);
        if (match(TokenType.NUMBER)) {
            return new ValueExpression(createNumber(current.getText(), 10));
        }
        if (match(TokenType.HEX_NUMBER)) {
            return new ValueExpression(createNumber(current.getText(), 16));
        }
        if (match(TokenType.TEXT)) {
            return new ValueExpression(current.getText());
        }
        throw new ParseException("Unknown expression: " + current);
    }
    
    private Number createNumber(String text, int radix) {
        // Double
        if (text.contains(".")) {
            return Double.parseDouble(text);
        }
        // Integer
        try {
            return Integer.parseInt(text, radix);
        } catch (NumberFormatException nfe) {
            return Long.parseLong(text, radix);
        }
    }
    
    private Token consume(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) throw new ParseException("Token " + current + " doesn't match " + type);
        pos++;
        return current;
    }
    
    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }
    
    private boolean lookMatch(int pos, TokenType type) {
        return get(pos).getType() == type;
    }
    
    private Token get(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }
}
