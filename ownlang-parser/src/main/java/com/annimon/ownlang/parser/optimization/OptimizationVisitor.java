package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.UserDefinedFunction;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.ast.*;
import static com.annimon.ownlang.parser.visitors.VisitorUtils.isValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OptimizationVisitor<T> implements ResultVisitor<Node, T> {

    @Override
    public Node visit(ArrayExpression s, T t) {
        final List<Node> elements = new ArrayList<>(s.elements.size());
        boolean changed = false;
        for (Node expression : s.elements) {
            final Node node = expression.accept(this, t);
            if (node != expression) {
                changed = true;
            }
            elements.add(node);
        }
        if (changed) {
            return new ArrayExpression(elements);
        }
        return s;
    }

    @Override
    public Node visit(AssignmentExpression s, T t) {
        final Node exprNode = s.expression.accept(this, t);
        final Node targetNode = s.target.accept(this, t);
        if ( (exprNode != s.expression || targetNode != s.target) && (targetNode instanceof Accessible) ) {
            return new AssignmentExpression(s.operation, (Accessible) targetNode, exprNode);
        }
        return s;
    }

    @Override
    public Node visit(BinaryExpression s, T t) {
        final Node expr1 = s.expr1.accept(this, t);
        final Node expr2 = s.expr2.accept(this, t);
        if (expr1 != s.expr1 || expr2 != s.expr2) {
            return new BinaryExpression(s.operation, expr1, expr2);
        }
        return s;
    }

    @Override
    public Node visit(BlockStatement s, T t) {
        boolean changed = false;
        final BlockStatement result = new BlockStatement();
        for (Node statement : s.statements) {
            final Node node = statement.accept(this, t);
            if (node != statement) {
                changed = true;
            }
            if (node != null) {
                result.add(consumeStatement(node));
            }
        }
        if (changed) {
            return result;
        }
        return s;
    }

    @Override
    public Node visit(BreakStatement s, T t) {
        return s;
    }

    @Override
    public Node visit(ClassDeclarationStatement s, T t) {
        final var newClassDeclaration = new ClassDeclarationStatement(s.name);
        boolean changed = false;
        for (AssignmentExpression field : s.fields) {
            final Node fieldExpr = field.expression.accept(this, t);
            final AssignmentExpression newField;
            if (fieldExpr != field.expression) {
                changed = true;
                newField = new AssignmentExpression(field.operation, field.target, fieldExpr);
            } else {
                newField = field;
            }
            newClassDeclaration.addField(newField);
        }

        for (FunctionDefineStatement method : s.methods) {
            final var newMethod = method.accept(this, t);
            if (newMethod != method) {
                changed = true;
                newClassDeclaration.addMethod((FunctionDefineStatement) newMethod);
            } else {
                newClassDeclaration.addMethod(method);
            }
        }

        if (changed) {
            return newClassDeclaration;
        }
        return s;
    }
    
    @Override
    public Node visit(ConditionalExpression s, T t) {
        final Node expr1 = s.expr1.accept(this, t);
        final Node expr2 = s.expr2.accept(this, t);
        if (expr1 != s.expr1 || expr2 != s.expr2) {
            return new ConditionalExpression(s.operation, expr1, expr2);
        }
        return s;
    }

    @Override
    public Node visit(ContainerAccessExpression s, T t) {
        final Node root = s.root.accept(this, t);
        boolean changed = (root != s.root);

        final List<Node> indices = new ArrayList<>(s.indices.size());
        for (Node expression : s.indices) {
            final Node node = expression.accept(this, t);
            if (node != expression) {
                changed = true;
            }
            indices.add(node);
        }
        if (changed) {
            return new ContainerAccessExpression(root, indices);
        }
        return s;
    }

    @Override
    public Node visit(ContinueStatement s, T t) {
        return s;
    }

    @Override
    public Node visit(DoWhileStatement s, T t) {
        final Node condition = s.condition.accept(this, t);
        final Node statement = s.statement.accept(this, t);
        if (condition != s.condition || statement != s.statement) {
            return new DoWhileStatement(condition, consumeStatement(statement));
        }
        return s;
    }

    @Override
    public Node visit(DestructuringAssignmentStatement s, T t) {
        final Node expr = s.containerExpression.accept(this, t);
        if (expr != s.containerExpression) {
            return new DestructuringAssignmentStatement(s.variables, expr);
        }
        return s;
    }

    @Override
    public Node visit(ForStatement s, T t) {
        final Node initialization = s.initialization.accept(this, t);
        final Node termination = s.termination.accept(this, t);
        final Node increment = s.increment.accept(this, t);
        final Node statement = s.statement.accept(this, t);
        if (initialization != s.initialization || termination != s.termination
                || increment != s.increment || statement != s.statement) {
            return new ForStatement(consumeStatement(initialization),
                    termination, consumeStatement(increment), consumeStatement(statement));
        }
        return s;
    }

    @Override
    public Node visit(ForeachArrayStatement s, T t) {
        final Node container = s.container.accept(this, t);
        final Node body = s.body.accept(this, t);
        if (container != s.container || body != s.body) {
            return new ForeachArrayStatement(s.variable, container, consumeStatement(body));
        }
        return s;
    }

    @Override
    public Node visit(ForeachMapStatement s, T t) {
        final Node container = s.container.accept(this, t);
        final Node body = s.body.accept(this, t);
        if (container != s.container || body != s.body) {
            return new ForeachMapStatement(s.key, s.value, container, consumeStatement(body));
        }
        return s;
    }

    @Override
    public Node visit(FunctionDefineStatement s, T t) {
        final Arguments newArgs = new Arguments();
        boolean changed = visit(s.arguments, newArgs, t);

        final Node body = s.body.accept(this, t);
        if (changed || body != s.body) {
            return new FunctionDefineStatement(s.name, newArgs, consumeStatement(body), s.getRange());
        }
        return s;
    }

    @Override
    public Node visit(FunctionReferenceExpression s, T t) {
        return s;
    }

    @Override
    public Node visit(ExprStatement s, T t) {
        final Node expr = s.expr.accept(this, t);
        if (expr != s.expr) {
            return new ExprStatement(expr);
        }
        return s;
    }

    @Override
    public Node visit(FunctionalExpression s, T t) {
        final Node functionExpr = s.functionExpr.accept(this, t);
        final FunctionalExpression result = new FunctionalExpression(functionExpr);
        boolean changed = functionExpr != s.functionExpr;
        for (Node argument : s.arguments) {
            final Node expr = argument.accept(this, t);
            if (expr != argument) {
                changed = true;
            }
            result.addArgument(expr);
        }
        if (changed) {
            return result;
        }
        return s;
    }

    @Override
    public Node visit(IfStatement s, T t) {
        final Node expression = s.expression.accept(this, t);
        final Node ifStatement = s.ifStatement.accept(this, t);
        final Node elseStatement;
        if (s.elseStatement != null) {
            elseStatement = s.elseStatement.accept(this, t);
        } else {
            elseStatement = null;
        }
        if (expression != s.expression || ifStatement != s.ifStatement || elseStatement != s.elseStatement) {
            return new IfStatement(expression, consumeStatement(ifStatement),
                    (elseStatement == null ? null : consumeStatement(elseStatement)) );
        }
        return s;
    }

    @Override
    public Node visit(IncludeStatement s, T t) {
        final Node expression = s.expression.accept(this, t);
        if (expression != s.expression) {
            return new IncludeStatement(expression);
        }
        return s;
    }

    @Override
    public Node visit(MapExpression s, T t) {
        final Map<Node, Node> elements = new HashMap<>(s.elements.size());
        boolean changed = false;
        for (Map.Entry<Node, Node> entry : s.elements.entrySet()) {
            final Node key = entry.getKey().accept(this, t);
            final Node value = entry.getValue().accept(this, t);
            if (key != entry.getKey() || value != entry.getValue()) {
                changed = true;
            }
            elements.put(key, value);
        }
        if (changed) {
            return new MapExpression(elements);
        }
        return s;
    }

    @Override
    public Node visit(MatchExpression s, T t) {
        final Node expression = s.expression.accept(this, t);
        boolean changed = expression != s.expression;
        final List<MatchExpression.Pattern> patterns = new ArrayList<>(s.patterns.size());
        for (MatchExpression.Pattern pattern : s.patterns) {
            if (pattern instanceof MatchExpression.VariablePattern varPattern) {
                final String variable = varPattern.variable;
                final VariableExpression expr = new VariableExpression(variable);
                final Node node = expr.accept(this, t);
                if ((node != expr) && isValue(node)) {
                    changed = true;
                    final Value value = ((ValueExpression) node).value;
                    final Node optCondition = pattern.optCondition;
                    final Statement result = pattern.result;
                    pattern = new MatchExpression.ConstantPattern(value);
                    pattern.optCondition = optCondition;
                    pattern.result = result;
                }
            }
            
            if (pattern instanceof MatchExpression.TuplePattern tuple) {
                final List<Node> newValues = new ArrayList<>(tuple.values.size());
                boolean valuesChanged = false;
                for (Node value : tuple.values) {
                    if (value != MatchExpression.ANY) {
                        final Node node = value.accept(this, t);
                        if (node != value) {
                            valuesChanged = true;
                            value = node;
                        }
                    }
                    newValues.add(value);
                }
                if (valuesChanged) {
                    changed = true;
                    final Node optCondition = pattern.optCondition;
                    final Statement result = pattern.result;
                    pattern = new MatchExpression.TuplePattern(newValues);
                    pattern.optCondition = optCondition;
                    pattern.result = result;
                }
            }

            final Node patternResult = pattern.result.accept(this, t);
            if (patternResult != pattern.result) {
                changed = true;
                pattern.result = consumeStatement(patternResult);
            }

            if (pattern.optCondition != null) {
                Node optCond = pattern.optCondition.accept(this, t);
                if (optCond != pattern.optCondition) {
                    changed = true;
                    pattern.optCondition = optCond;
                }
            }

            patterns.add(pattern);
        }
        if (changed) {
            return new MatchExpression(expression, patterns);
        }
        return s;
    }
    
    @Override
    public Node visit(ObjectCreationExpression s, T t) {
        final List<Node> args = new ArrayList<>();
        boolean changed = false;
        for (Node argument : s.constructorArguments) {
            final Node expr = argument.accept(this, t);
            if (expr != argument) {
                changed = true;
            }
            args.add(expr);
        }
        
        if (changed) {
            return new ObjectCreationExpression(s.className, args);
        }
        return s;
    }

    @Override
    public Node visit(PrintStatement s, T t) {
        final Node expression = s.expression.accept(this, t);
        if (expression != s.expression) {
            return new PrintStatement(expression);
        }
        return s;
    }

    @Override
    public Node visit(PrintlnStatement s, T t) {
        final Node expression = s.expression.accept(this, t);
        if (expression != s.expression) {
            return new PrintlnStatement(expression);
        }
        return s;
    }

    @Override
    public Node visit(ReturnStatement s, T t) {
        final Node expression = s.expression.accept(this, t);
        if (expression != s.expression) {
            return new ReturnStatement(expression);
        }
        return s;
    }

    @Override
    public Node visit(TernaryExpression s, T t) {
        final Node condition = s.condition.accept(this, t);
        final Node trueExpr = s.trueExpr.accept(this, t);
        final Node falseExpr = s.falseExpr.accept(this, t);
        if (condition != s.condition || trueExpr != s.trueExpr || falseExpr != s.falseExpr) {
            return new TernaryExpression(condition, trueExpr, falseExpr);
        }
        return s;
    }

    @Override
    public Node visit(UnaryExpression s, T t) {
        final Node expr1 = s.expr1.accept(this, t);
        if (expr1 != s.expr1) {
            return new UnaryExpression(s.operation, expr1);
        }
        return s;
    }

    @Override
    public Node visit(ValueExpression s, T t) {
        if ( (s.value.type() == Types.FUNCTION) && (s.value.raw() instanceof UserDefinedFunction function) ) {
            final UserDefinedFunction accepted = visit(function, t);
            if (accepted != function) {
                return new ValueExpression(accepted);
            }
        }
        return s;
    }

    @Override
    public Node visit(VariableExpression s, T t) {
        return s;
    }

    @Override
    public Node visit(WhileStatement s, T t) {
        final Node condition = s.condition.accept(this, t);
        final Node statement = s.statement.accept(this, t);
        if (condition != s.condition || statement != s.statement) {
            return new WhileStatement(condition, consumeStatement(statement));
        }
        return s;
    }

    @Override
    public Node visit(UseStatement s, T t) {
        return s;
    }

    public UserDefinedFunction visit(UserDefinedFunction s, T t) {
        final Arguments newArgs = new Arguments();
        boolean changed = visit(s.arguments, newArgs, t);

        final Node body = s.body.accept(this, t);
        if (changed || body != s.body) {
            return new UserDefinedFunction(newArgs, consumeStatement(body), s.getRange());
        }
        return s;
    }



    protected boolean visit(final Arguments in, final Arguments out, T t) {
        boolean changed = false;
        out.setRange(in.getRange());
        for (Argument argument : in) {
            final Node valueExpr = argument.valueExpr();
            if (valueExpr == null) {
                out.addRequired(argument.name());
            } else {
                final Node expr = valueExpr.accept(this, t);
                if (expr != valueExpr) {
                    changed = true;
                }
                out.addOptional(argument.name(), expr);
            }
        }
        return changed;
    }

    protected Statement consumeStatement(Node node) {
        if (node instanceof Statement statement) {
            return statement;
        }
        return new ExprStatement(node);
    }
}
