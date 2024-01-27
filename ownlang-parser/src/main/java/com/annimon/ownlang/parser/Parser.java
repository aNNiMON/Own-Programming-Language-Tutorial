package com.annimon.ownlang.parser;

import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.exceptions.ParseException;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.UserDefinedFunction;
import com.annimon.ownlang.parser.ast.*;
import com.annimon.ownlang.parser.error.ParseError;
import com.annimon.ownlang.parser.error.ParseErrors;
import com.annimon.ownlang.util.Pos;
import com.annimon.ownlang.util.Range;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author aNNiMON
 */
public final class Parser {

    public static Node parse(List<Token> tokens) {
        final Parser parser = new Parser(tokens);
        final Node program = parser.parse();
        if (parser.getParseErrors().hasErrors()) {
            throw new OwnLangParserException(parser.getParseErrors());
        }
        return program;
    }

    private static final Token EOF = new Token(TokenType.EOF, "", Pos.UNKNOWN);

    private static final EnumMap<TokenType, BinaryExpression.Operator> ASSIGN_OPERATORS;
    static {
        ASSIGN_OPERATORS = new EnumMap<>(TokenType.class);
        ASSIGN_OPERATORS.put(TokenType.EQ, null);
        ASSIGN_OPERATORS.put(TokenType.PLUSEQ, BinaryExpression.Operator.ADD);
        ASSIGN_OPERATORS.put(TokenType.MINUSEQ, BinaryExpression.Operator.SUBTRACT);
        ASSIGN_OPERATORS.put(TokenType.STAREQ, BinaryExpression.Operator.MULTIPLY);
        ASSIGN_OPERATORS.put(TokenType.SLASHEQ, BinaryExpression.Operator.DIVIDE);
        ASSIGN_OPERATORS.put(TokenType.PERCENTEQ, BinaryExpression.Operator.REMAINDER);
        ASSIGN_OPERATORS.put(TokenType.AMPEQ, BinaryExpression.Operator.AND);
        ASSIGN_OPERATORS.put(TokenType.CARETEQ, BinaryExpression.Operator.XOR);
        ASSIGN_OPERATORS.put(TokenType.BAREQ, BinaryExpression.Operator.OR);
        ASSIGN_OPERATORS.put(TokenType.COLONCOLONEQ, BinaryExpression.Operator.PUSH);
        ASSIGN_OPERATORS.put(TokenType.LTLTEQ, BinaryExpression.Operator.LSHIFT);
        ASSIGN_OPERATORS.put(TokenType.GTGTEQ, BinaryExpression.Operator.RSHIFT);
        ASSIGN_OPERATORS.put(TokenType.GTGTGTEQ, BinaryExpression.Operator.URSHIFT);
        ASSIGN_OPERATORS.put(TokenType.ATEQ, BinaryExpression.Operator.AT);
    }

    private final List<Token> tokens;
    private final int size;
    private final ParseErrors parseErrors;
    private Statement parsedStatement;

    private int index;

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

    public Node parse() {
        parseErrors.clear();
        final BlockStatement result = new BlockStatement();
        while (!match(TokenType.EOF)) {
            try {
                result.add(statement());
            } catch (ParseException ex) {
                parseErrors.add(new ParseError(ex.getMessage(), ex.getRange()));
                recover();
            } catch (Exception ex) {
                parseErrors.add(new ParseError(ex.getMessage(), getRange(), ex.getStackTrace()));
                recover();
            }
        }
        parsedStatement = result;
        return result;
    }

    private void recover() {
        int preRecoverIndex = index;
        for (int i = preRecoverIndex; i <= size; i++) {
            index = i;
            try {
                statement();
                // successfully parsed,
                index = i; // restore position
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
            return new BreakStatement(getRange(index - 1, index - 1));
        }
        if (match(TokenType.CONTINUE)) {
            return new ContinueStatement(getRange(index - 1, index - 1));
        }
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(expression());
        }
        if (match(TokenType.USE)) {
            return useStatement();
        }
        if (match(TokenType.INCLUDE)) {
            return includeStatement();
        }
        if (match(TokenType.FOR)) {
            return forStatement();
        }
        if (match(TokenType.DEF)) {
            return functionDefine();
        }
        if (match(TokenType.MATCH)) {
            return match();
        }
        if (match(TokenType.CLASS)) {
            return classDeclaration();
        }
        if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN)) {
            return functionCallStatement();
        }
        return assignmentStatement();
    }

    private IncludeStatement includeStatement() {
        final var startTokenIndex = index - 1;
        final var include = new IncludeStatement(expression());
        include.setRange(getRange(startTokenIndex, index));
        return include;
    }

    private UseStatement useStatement() {
        final var modules = new HashSet<String>();
        do {
            modules.add(consumeOrExplainError(TokenType.WORD,
                    Parser::explainUseStatementError).text());
        } while (match(TokenType.COMMA));
        return new UseStatement(modules);
    }

    private Statement assignmentStatement() {
        if (match(TokenType.EXTRACT)) {
            return destructuringAssignment();
        }
        final Node expression = expression();
        if (expression instanceof Statement statement) {
            return statement;
        }
        throw error("Unknown statement: " + get(0));
    }

    private DestructuringAssignmentStatement destructuringAssignment() {
        // extract(var1, var2, ...) = ...
        final var startTokenIndex = index;
        consume(TokenType.LPAREN);
        final List<String> variables = new ArrayList<>();
        while (!match(TokenType.RPAREN)) {
            final Token current = get(0);
            variables.add(switch (current.type()) {
                case WORD -> consume(TokenType.WORD).text();
                case COMMA -> null;
                default -> throw error(errorUnexpectedTokens(current, TokenType.WORD, TokenType.COMMA));
            });
            match(TokenType.COMMA);
        }
        if (variables.isEmpty() || variables.stream().allMatch(Objects::isNull)) {
            throw error(errorDestructuringAssignmentEmpty(), startTokenIndex, index);
        }
        consume(TokenType.EQ);
        return new DestructuringAssignmentStatement(variables, expression());
    }

    private Statement ifElse() {
        final Node condition = expression();
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
        final Node condition = expression();
        final Statement statement = statementOrBlock();
        return new WhileStatement(condition, statement);
    }

    private Statement doWhileStatement() {
        final Statement statement = statementOrBlock();
        consume(TokenType.WHILE);
        final Node condition = expression();
        return new DoWhileStatement(condition, statement);
    }

    private Statement forStatement() {
        int foreachIndex = lookMatch(0, TokenType.LPAREN) ? 1 : 0;
        if (lookMatch(foreachIndex, TokenType.WORD)
                && lookMatch(foreachIndex + 1, TokenType.COLON)) {
            // for v : arr || for (v : arr)
            return foreachArrayStatement();
        }
        if (lookMatch(foreachIndex, TokenType.WORD)
                && lookMatch(foreachIndex + 1, TokenType.COMMA)
                && lookMatch(foreachIndex + 2, TokenType.WORD)
                && lookMatch(foreachIndex + 3, TokenType.COLON)) {
            // for key, value : arr || for (key, value : arr)
            return foreachMapStatement();
        }

        // for (init, condition, increment) body
        boolean optParentheses = match(TokenType.LPAREN);
        final Statement initialization = assignmentStatement();
        consume(TokenType.COMMA);
        final Node termination = expression();
        consume(TokenType.COMMA);
        final Statement increment = assignmentStatement();
        if (optParentheses) consume(TokenType.RPAREN); // close opt parentheses
        final Statement statement = statementOrBlock();
        return new ForStatement(initialization, termination, increment, statement);
    }

    private ForeachArrayStatement foreachArrayStatement() {
        // for x : arr
        boolean optParentheses = match(TokenType.LPAREN);
        final String variable = consume(TokenType.WORD).text();
        consume(TokenType.COLON);
        final Node container = expression();
        if (optParentheses) {
            consume(TokenType.RPAREN); // close opt parentheses
        }
        final Statement statement = statementOrBlock();
        return new ForeachArrayStatement(variable, container, statement);
    }

    private ForeachMapStatement foreachMapStatement() {
        // for k, v : map
        boolean optParentheses = match(TokenType.LPAREN);
        final String key = consume(TokenType.WORD).text();
        consume(TokenType.COMMA);
        final String value = consume(TokenType.WORD).text();
        consume(TokenType.COLON);
        final Node container = expression();
        if (optParentheses) {
            consume(TokenType.RPAREN); // close opt parentheses
        }
        final Statement statement = statementOrBlock();
        return new ForeachMapStatement(key, value, container, statement);
    }

    private FunctionDefineStatement functionDefine() {
        // def name(arg1, arg2 = value) { ... }  ||  def name(args) = expr
        final var startTokenIndex = index - 1;
        final String name = consume(TokenType.WORD).text();
        final Arguments arguments = arguments();
        final Statement body = statementBody();
        return new FunctionDefineStatement(name, arguments, body, getRange(startTokenIndex, index - 1));
    }

    private Arguments arguments() {
        // (arg1, arg2, arg3 = expr1, arg4 = expr2)
        final Arguments arguments = new Arguments();
        boolean startsOptionalArgs = false;
        final var startTokenIndex = index;
        consume(TokenType.LPAREN);
        while (!match(TokenType.RPAREN)) {
            final String name = consume(TokenType.WORD).text();
            if (match(TokenType.EQ)) {
                startsOptionalArgs = true;
                arguments.addOptional(name, variable());
            } else if (!startsOptionalArgs) {
                arguments.addRequired(name);
            } else {
                throw error(errorRequiredArgumentAfterOptional());
            }
            match(TokenType.COMMA);
        }
        arguments.setRange(getRange(startTokenIndex, index - 1));
        return arguments;
    }

    private Statement statementBody() {
        if (match(TokenType.EQ)) {
            return new ReturnStatement(expression());
        }
        return statementOrBlock();
    }

    private ExprStatement functionCallStatement() {
        return new ExprStatement(
                functionChain(new ValueExpression(consume(TokenType.WORD).text()))
        );
    }

    private Node functionChain(Node qualifiedNameExpr) {
        // f1()()() || f1().f2().f3() || f1().key
        final Node expr = function(qualifiedNameExpr);
        if (lookMatch(0, TokenType.LPAREN)) {
            return functionChain(expr);
        }
        if (lookMatch(0, TokenType.DOT)) {
            final List<Node> indices = variableSuffix();
            if (indices == null || indices.isEmpty()) {
                return expr;
            }

            if (lookMatch(0, TokenType.LPAREN)) {
                // next function call
                return functionChain(new ContainerAccessExpression(expr, indices, getRange()));
            }
            // container access
            return new ContainerAccessExpression(expr, indices, getRange());
        }
        return expr;
    }

    private FunctionalExpression function(Node qualifiedNameExpr) {
        // function(arg1, arg2, ...)
        final var startTokenIndex = index - 1;
        consume(TokenType.LPAREN);
        final FunctionalExpression function = new FunctionalExpression(qualifiedNameExpr);
        while (!match(TokenType.RPAREN)) {
            function.addArgument(expression());
            match(TokenType.COMMA);
        }
        function.setRange(getRange(startTokenIndex, index - 1));
        return function;
    }

    private Node array() {
        // [value1, value2, ...]
        consume(TokenType.LBRACKET);
        final List<Node> elements = new ArrayList<>();
        while (!match(TokenType.RBRACKET)) {
            elements.add(expression());
            match(TokenType.COMMA);
        }
        return new ArrayExpression(elements);
    }

    private Node map() {
        // {key1 : value1, key2 : value2, ...}
        consume(TokenType.LBRACE);
        final Map<Node, Node> elements = new LinkedHashMap<>();
        while (!match(TokenType.RBRACE)) {
            final Node key = primary();
            consume(TokenType.COLON);
            final Node value = expression();
            elements.put(key, value);
            match(TokenType.COMMA);
        }
        return new MapExpression(elements);
    }

    private MatchExpression match() {
        // match expression {
        //  case pattern1: result1
        //  case pattern2 if expr: result2
        // }
        final Node expression = expression();
        consume(TokenType.LBRACE);
        final List<MatchExpression.Pattern> patterns = new ArrayList<>();
        do {
            consume(TokenType.CASE);
            MatchExpression.Pattern pattern = null;
            final Token current = get(0);
            if (match(TokenType.NUMBER)) {
                // case 20:
                pattern = new MatchExpression.ConstantPattern(
                        NumberValue.of(createNumber(current.text(), 10))
                );
            } else if (match(TokenType.DECIMAL_NUMBER)) {
                // case 0.5:
                pattern = new MatchExpression.ConstantPattern(
                        NumberValue.of(createDecimalNumber(current.text()))
                );
            } else if (match(TokenType.HEX_NUMBER)) {
                // case #FF:
                pattern = new MatchExpression.ConstantPattern(
                        NumberValue.of(createNumber(current.text(), 16))
                );
            } else if (match(TokenType.TEXT)) {
                // case "text":
                pattern = new MatchExpression.ConstantPattern(
                        new StringValue(current.text())
                );
            } else if (match(TokenType.WORD)) {
                // case value:
                pattern = new MatchExpression.VariablePattern(current.text());
            } else if (match(TokenType.LBRACKET)) {
                // case [x :: xs]:
                final MatchExpression.ListPattern listPattern = new MatchExpression.ListPattern();
                while (!match(TokenType.RBRACKET)) {
                    listPattern.add(consume(TokenType.WORD).text());
                    match(TokenType.COLONCOLON);
                }
                pattern = listPattern;
            } else if (match(TokenType.LPAREN)) {
                // case (1, 2):
                final MatchExpression.TuplePattern tuplePattern = new MatchExpression.TuplePattern();
                while (!match(TokenType.RPAREN)) {
                    if ("_".equals(get(0).text())) {
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
                throw error("Wrong pattern in match expression: " + current);
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
    
    private Statement classDeclaration() {
        // class Name {
        //   x = 123
        //   str = ""
        //   def method() = str
        // }
        final String name = consume(TokenType.WORD).text();
        final ClassDeclarationStatement classDeclaration = new ClassDeclarationStatement(name);
        consume(TokenType.LBRACE);
        do {
            if (match(TokenType.DEF)) {
                classDeclaration.addMethod(functionDefine());
            } else {
                final AssignmentExpression fieldDeclaration = assignmentStrict();
                if (fieldDeclaration != null) {
                    classDeclaration.addField(fieldDeclaration);
                } else {
                    throw error("Class can contain only assignments and function declarations");
                }
            }
        } while (!match(TokenType.RBRACE));
        return classDeclaration;
    }

    private Node expression() {
        return assignment();
    }

    private Node assignment() {
        final Node assignment = assignmentStrict();
        if (assignment != null) {
            return assignment;
        }
        return ternary();
    }

    private AssignmentExpression assignmentStrict() {
        // x[0].prop += ...
        final int position = index;
        final Node targetExpr = qualifiedName();
        if (!(targetExpr instanceof Accessible)) {
            index = position;
            return null;
        }

        final TokenType currentType = get(0).type();
        if (!ASSIGN_OPERATORS.containsKey(currentType)) {
            index = position;
            return null;
        }
        match(currentType);

        final BinaryExpression.Operator op = ASSIGN_OPERATORS.get(currentType);
        final Node expression = expression();

        return new AssignmentExpression(op, (Accessible) targetExpr, expression, getRange(position, index));
    }

    private Node ternary() {
        Node result = nullCoalesce();

        if (match(TokenType.QUESTION)) {
            final Node trueExpr = expression();
            consume(TokenType.COLON);
            final Node falseExpr = expression();
            return new TernaryExpression(result, trueExpr, falseExpr);
        }
        if (match(TokenType.QUESTIONCOLON)) {
            return new BinaryExpression(BinaryExpression.Operator.ELVIS, result, expression());
        }
        return result;
    }

    private Node nullCoalesce() {
        Node result = logicalOr();

        while (true) {
            if (match(TokenType.QUESTIONQUESTION)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.NULL_COALESCE, result, expression());
                continue;
            }
            break;
        }

        return result;
    }

    private Node logicalOr() {
        Node result = logicalAnd();

        while (true) {
            if (match(TokenType.BARBAR)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.OR, result, logicalAnd());
                continue;
            }
            break;
        }

        return result;
    }

    private Node logicalAnd() {
        Node result = bitwiseOr();

        while (true) {
            if (match(TokenType.AMPAMP)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.AND, result, bitwiseOr());
                continue;
            }
            break;
        }

        return result;
    }

    private Node bitwiseOr() {
        Node expression = bitwiseXor();

        while (true) {
            if (match(TokenType.BAR)) {
                expression = new BinaryExpression(BinaryExpression.Operator.OR, expression, bitwiseXor());
                continue;
            }
            break;
        }

        return expression;
    }

    private Node bitwiseXor() {
        Node expression = bitwiseAnd();

        while (true) {
            if (match(TokenType.CARET)) {
                expression = new BinaryExpression(BinaryExpression.Operator.XOR, expression, bitwiseAnd());
                continue;
            }
            break;
        }

        return expression;
    }

    private Node bitwiseAnd() {
        Node expression = equality();

        while (true) {
            if (match(TokenType.AMP)) {
                expression = new BinaryExpression(BinaryExpression.Operator.AND, expression, equality());
                continue;
            }
            break;
        }

        return expression;
    }

    private Node equality() {
        Node result = conditional();

        if (match(TokenType.EQEQ)) {
            return new ConditionalExpression(ConditionalExpression.Operator.EQUALS, result, conditional());
        }
        if (match(TokenType.EXCLEQ)) {
            return new ConditionalExpression(ConditionalExpression.Operator.NOT_EQUALS, result, conditional());
        }

        return result;
    }

    private Node conditional() {
        Node result = shift();

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

    private Node shift() {
        Node expression = additive();

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

    private Node additive() {
        Node result = multiplicative();

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
            if (match(TokenType.CARETCARET)) {
                result = new BinaryExpression(BinaryExpression.Operator.CARETCARET, result, multiplicative());
                continue;
            }
            break;
        }

        return result;
    }

    private Node multiplicative() {
        Node result = objectCreation();

        while (true) {
            if (match(TokenType.STAR)) {
                result = new BinaryExpression(BinaryExpression.Operator.MULTIPLY, result, objectCreation());
                continue;
            }
            if (match(TokenType.SLASH)) {
                result = new BinaryExpression(BinaryExpression.Operator.DIVIDE, result, objectCreation());
                continue;
            }
            if (match(TokenType.PERCENT)) {
                result = new BinaryExpression(BinaryExpression.Operator.REMAINDER, result, objectCreation());
                continue;
            }
            if (match(TokenType.STARSTAR)) {
                result = new BinaryExpression(BinaryExpression.Operator.POWER, result, objectCreation());
                continue;
            }
            break;
        }

        return result;
    }
    
    private Node objectCreation() {
        if (match(TokenType.NEW)) {
            final var startTokenIndex = index - 1;
            final String className = consume(TokenType.WORD).text();
            final List<Node> args = new ArrayList<>();
            consume(TokenType.LPAREN);
            while (!match(TokenType.RPAREN)) {
                args.add(expression());
                match(TokenType.COMMA);
            }
            return new ObjectCreationExpression(className, args, getRange(startTokenIndex, index - 1));
        }
        
        return unary();
    }

    private Node unary() {
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

    private Node primary() {
        if (match(TokenType.LPAREN)) {
            Node result = expression();
            consume(TokenType.RPAREN);
            return result;
        }

        if (match(TokenType.COLONCOLON)) {
            // ::method reference
            final String functionName = consume(TokenType.WORD).text();
            return new FunctionReferenceExpression(functionName);
        }
        if (match(TokenType.MATCH)) {
            return match();
        }
        if (match(TokenType.DEF)) {
            // anonymous function def(args) ...
            final var startTokenIndex = index - 1;
            final Arguments arguments = arguments();
            final Statement statement = statementBody();
            final Range range = getRange(startTokenIndex, index - 1);
            return new ValueExpression(new UserDefinedFunction(arguments, statement, range));
        }
        return variable();
    }

    private Node variable() {
        // function(...
        if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN)) {
            return functionChain(new ValueExpression(consume(TokenType.WORD).text()));
        }

        final Node qualifiedNameExpr = qualifiedName();
        if (qualifiedNameExpr != null) {
            // variable(args) || arr["key"](args) || obj.key(args)
            if (lookMatch(0, TokenType.LPAREN)) {
                return functionChain(qualifiedNameExpr);
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

    private Node qualifiedName() {
        // var || var.key[index].key2
        final var startTokenIndex = index;
        final Token current = get(0);
        if (!match(TokenType.WORD)) return null;

        final List<Node> indices = variableSuffix();
        final var variable = new VariableExpression(current.text());
        variable.setRange(getRange(startTokenIndex, index - 1));
        if (indices == null || indices.isEmpty()) {
            return variable;
        } else {
            return new ContainerAccessExpression(variable, indices, variable.getRange());
        }
    }

    private List<Node> variableSuffix() {
        // .key1.arr1[expr1][expr2].key2
        if (!lookMatch(0, TokenType.DOT) && !lookMatch(0, TokenType.LBRACKET)) {
            return null;
        }
        final List<Node> indices = new ArrayList<>();
        while (lookMatch(0, TokenType.DOT) || lookMatch(0, TokenType.LBRACKET)) {
            if (match(TokenType.DOT)) {
                final String fieldName = consume(TokenType.WORD).text();
                final Node key = new ValueExpression(fieldName);
                indices.add(key);
            }
            if (match(TokenType.LBRACKET)) {
                indices.add(expression());
                consume(TokenType.RBRACKET);
            }
        }
        return indices;
    }

    private Node value() {
        final Token current = get(0);
        if (match(TokenType.NUMBER)) {
            return new ValueExpression(createNumber(current.text(), 10));
        }
        if (match(TokenType.DECIMAL_NUMBER)) {
            return new ValueExpression(createDecimalNumber(current.text()));
        }
        if (match(TokenType.HEX_NUMBER)) {
            return new ValueExpression(createNumber(current.text(), 16));
        }
        if (match(TokenType.TEXT)) {
            final ValueExpression strExpr = new ValueExpression(current.text());
            // "text".property || "text".func()
            if (lookMatch(0, TokenType.DOT)) {
                if (lookMatch(1, TokenType.WORD) && lookMatch(2, TokenType.LPAREN)) {
                    match(TokenType.DOT);
                    return functionChain(new ContainerAccessExpression(
                            strExpr,
                            Collections.singletonList(new ValueExpression(consume(TokenType.WORD).text())),
                            getRange()
                    ));
                }
                final List<Node> indices = variableSuffix();
                if (indices == null || indices.isEmpty()) {
                    return strExpr;
                }
                return new ContainerAccessExpression(strExpr, indices, getRange());
            }
            return strExpr;
        }
        throw error("Unknown expression: " + current);
    }

    private Number createNumber(String text, int radix) {
        // Integer
        try {
            return Integer.parseInt(text, radix);
        } catch (NumberFormatException nfe) {
            return Long.parseLong(text, radix);
        }
    }

    private Number createDecimalNumber(String text) {
        // Double
        return Double.parseDouble(text);
    }

    private Token consume(TokenType expectedType) {
        final Token actual = get(0);
        if (expectedType != actual.type()) {
            throw error(errorUnexpectedToken(actual, expectedType));
        }
        index++;
        return actual;
    }

    private Token consumeOrExplainError(TokenType expectedType, Function<Token, String> errorMessageFunction) {
        final Token actual = get(0);
        if (expectedType != actual.type()) {
            throw error(errorUnexpectedToken(actual, expectedType)
                    + errorMessageFunction.apply(actual));
        }
        index++;
        return actual;
    }

    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.type()) {
            return false;
        }
        index++;
        return true;
    }

    private boolean lookMatch(int pos, TokenType type) {
        return get(pos).type() == type;
    }

    private Token get(int relativePosition) {
        final int position = index + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }

    private Range getRange() {
        return getRange(index, index);
    }

    private Range getRange(int startIndex, int endIndex) {
        if (size == 0) return Range.ZERO;
        final int last = size - 1;
        Pos start = tokens.get(Math.min(startIndex, last)).pos();
        if (startIndex == endIndex) {
            return new Range(start, start);
        } else {
            Pos end = tokens.get(Math.min(endIndex, last)).pos();
            return new Range(start, end);
        }
    }

    private ParseException error(String message) {
        return new ParseException(message, getRange());
    }

    private ParseException error(String message, int startIndex, int endIndex) {
        return new ParseException(message, getRange(startIndex, endIndex));
    }

    private static String errorUnexpectedToken(Token actual, TokenType expectedType) {
        return "Expected token with type " + expectedType + ", but found " + actual.shortDescription();
    }

    private static String errorUnexpectedTokens(Token actual, TokenType... expectedTypes) {
        String tokenTypes = Arrays.stream(expectedTypes).map(Enum::toString).collect(Collectors.joining(", "));
        return "Expected tokens with types one of " + tokenTypes + ", but found " + actual.shortDescription();
    }

    private static String errorDestructuringAssignmentEmpty() {
        return "Destructuring assignment should contain at least one variable name to assign." +
                "\nCorrect syntax: extract(v1, , , v4) = ";
    }

    private static String errorRequiredArgumentAfterOptional() {
        return "Required argument cannot be placed after optional.";
    }

    private static String explainUseStatementError(Token current) {
        String example = current.type().equals(TokenType.TEXT)
                ? "use " + current.text()
                : "use std, math";
        return "\nNote: as of OwnLang 2.0.0 use statement simplifies modules list syntax. " +
                "Correct syntax: " + example;
    }
}
