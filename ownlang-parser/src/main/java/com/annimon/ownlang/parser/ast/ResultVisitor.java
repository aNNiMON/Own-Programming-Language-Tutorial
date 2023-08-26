package com.annimon.ownlang.parser.ast;

/**
 *
 * @author aNNiMON
 * @param <R> the result type
 * @param <T> the type if the input
 */
public interface ResultVisitor<R, T> {
    
    R visit(ArrayExpression s, T t);
    R visit(AssignmentExpression s, T t);
    R visit(BinaryExpression s, T t);
    R visit(BlockStatement s, T t);
    R visit(BreakStatement s, T t);
    R visit(ClassDeclarationStatement s, T t);
    R visit(ConditionalExpression s, T t);
    R visit(ContainerAccessExpression s, T t);
    R visit(ContinueStatement s, T t);
    R visit(DoWhileStatement s, T t);
    R visit(DestructuringAssignmentStatement s, T t);
    R visit(ForStatement s, T t);
    R visit(ForeachArrayStatement s, T t);
    R visit(ForeachMapStatement s, T t);
    R visit(FunctionDefineStatement s, T t);
    R visit(FunctionReferenceExpression s, T t);
    R visit(ExprStatement s, T t);
    R visit(FunctionalExpression s, T t);
    R visit(IfStatement s, T t);
    R visit(IncludeStatement s, T t);
    R visit(MapExpression s, T t);
    R visit(MatchExpression s, T t);
    R visit(ObjectCreationExpression s, T t);
    R visit(PrintStatement s, T t);
    R visit(PrintlnStatement s, T t);
    R visit(ReturnStatement s, T t);
    R visit(TernaryExpression s, T t);
    R visit(UnaryExpression s, T t);
    R visit(ValueExpression s, T t);
    R visit(VariableExpression s, T t);
    R visit(WhileStatement s, T t);
    R visit(UseStatement s, T t);
}
