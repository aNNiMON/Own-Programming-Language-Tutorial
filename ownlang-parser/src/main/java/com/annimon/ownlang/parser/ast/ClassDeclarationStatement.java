package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.List;

public final class ClassDeclarationStatement implements Statement {
    
    public final String name;
    public final List<FunctionDefineStatement> methods;
    public final List<AssignmentExpression> fields;
    
    public ClassDeclarationStatement(String name) {
        this.name = name;
        methods = new ArrayList<>();
        fields = new ArrayList<>();
    }
    
    public void addField(AssignmentExpression expr) {
        fields.add(expr);
    }

    public void addMethod(FunctionDefineStatement statement) {
        methods.add(statement);
    }

    @Override
    public Value eval() {
        final var classFields = fields.stream()
                .map(this::toClassField)
                .toList();
        final var classMethods = methods.stream()
                .map(this::toClassMethod)
                .toList();
        final var declaration = new ClassDeclaration(name, classFields, classMethods);
        ScopeHandler.setClassDeclaration(declaration);
        return NumberValue.ZERO;
    }

    private ClassField toClassField(AssignmentExpression f) {
        // TODO check only variable assignments
        final String fieldName = ((VariableExpression) f.target).name;
        return new ClassField(fieldName, f);
    }

    private ClassMethod toClassMethod(FunctionDefineStatement m) {
        final var function = new UserDefinedFunction(m.arguments, m.body, m.getRange());
        return new ClassMethod(m.name, function);
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return String.format("class %s {\n  %s  %s}", name, fields, methods);
    }
}
