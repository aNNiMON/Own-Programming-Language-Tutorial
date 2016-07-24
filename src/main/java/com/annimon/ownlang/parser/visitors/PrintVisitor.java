package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.parser.ast.*;
import java.util.Iterator;

public class PrintVisitor implements ResultVisitor<StringBuilder, StringBuilder> {

    private int indent;

    public PrintVisitor() {
        indent = -2;
    }

    @Override
    public StringBuilder visit(ArrayExpression s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(AssignmentExpression s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        ((Node) s.target).accept(this, t);
        t.append(' ').append((s.operation == null) ? "" : s.operation);
        t.append("= ");
        s.expression.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(BinaryExpression s, StringBuilder t) {
        s.expr1.accept(this, t);
        t.append(' ').append(s.operation).append(' ');
        s.expr2.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(BlockStatement s, StringBuilder t) {
        if (indent > 0) {
            t.append('{');
        }
        increaseIndent();
        for (Statement statement : s.statements) {
            statement.accept(this, t);
        }
        decreaseIndent();
        if (indent > 0) {
            newLine(t);
            t.append('}');
        }
        return t;
    }

    @Override
    public StringBuilder visit(BreakStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("break");
        return t;
    }

    @Override
    public StringBuilder visit(ConditionalExpression s, StringBuilder t) {
        s.expr1.accept(this, t);
        t.append(' ').append(s.operation.getName()).append(' ');
        s.expr2.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(ContainerAccessExpression s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(ContinueStatement s, StringBuilder t) {
        printIndent(t);
        t.append("continue");
        newLine(t);
        return t;
    }

    @Override
    public StringBuilder visit(DoWhileStatement s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(DestructuringAssignmentStatement s, StringBuilder t) {
        printIndent(t);
        t.append("extract (");
        final Iterator<String> it = s.variables.iterator();
        if (it.hasNext()) {
            String variable = it.next();
            t.append(variable == null ? " " : variable);
            while (it.hasNext()) {
                variable = it.next();
                t.append(variable == null ? " " : variable);
            }
        }
        t.append(") = ");
        s.containerExpression.accept(this, t);
        newLine(t);
        return t;
    }

    @Override
    public StringBuilder visit(ForStatement s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(ForeachArrayStatement s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(ForeachMapStatement s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(FunctionDefineStatement s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(FunctionReferenceExpression s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(ExprStatement s, StringBuilder t) {
        printIndent(t);
        s.expr.accept(this, t);
        newLine(t);
        return t;
    }

    @Override
    public StringBuilder visit(FunctionalExpression s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(IfStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("if (");
        s.expression.accept(this, t);
        t.append(") ");
        increaseIndent();
        s.ifStatement.accept(this, t);
        decreaseIndent();
        if (s.elseStatement != null) {
            newLine(t);
            printIndent(t);
            t.append("else ");
            increaseIndent();
            s.elseStatement.accept(this, t);
            decreaseIndent();
        }
        return t;
    }

    @Override
    public StringBuilder visit(IncludeStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("include ");
        s.expression.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(MapExpression s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(MatchExpression s, StringBuilder t) {
        return t;
    }

    @Override
    public StringBuilder visit(PrintStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("print ");
        s.expression.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(PrintlnStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("println ");
        s.expression.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(ReturnStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("return ");
        s.expression.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(TernaryExpression s, StringBuilder t) {
        s.condition.accept(this, t);
        t.append(" ? ");
        s.trueExpr.accept(this, t);
        t.append(" : ");
        s.falseExpr.accept(this, t);
        return t;
    }

    @Override
    public StringBuilder visit(UnaryExpression s, StringBuilder t) {
        switch (s.operation) {
            case INCREMENT_POSTFIX:
            case DECREMENT_POSTFIX:
                s.expr1.accept(this, t);
                t.append(s.operation);
            default:
                t.append(s.operation);
                s.expr1.accept(this, t);
        }
        return t;
    }

    @Override
    public StringBuilder visit(ValueExpression s, StringBuilder t) {
        switch (s.value.type()) {
            case Types.STRING:
                String str = s.value.raw().toString();
                str = str.replace("\n", "\\n");
                str = str.replace("\t", "\\t");
                t.append('"').append(str).append('"');
                break;
            default:
                t.append(s.value.raw());
                break;
        }
        return t;
    }

    @Override
    public StringBuilder visit(VariableExpression s, StringBuilder t) {
        boolean extendedWordVariable = false;
        for (char ch : s.name.toCharArray()) {
            if (!Character.isLetterOrDigit(ch)) {
                extendedWordVariable = true;
                break;
            }
        }
        if (extendedWordVariable) {
            t.append('`').append(s.name).append('`');
        } else {
            t.append(s.name);
        }
        return t;
    }

    @Override
    public StringBuilder visit(WhileStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("while (");
        s.condition.accept(this, t);
        t.append(") {");
        newLine(t);
        increaseIndent();
        s.statement.accept(this, t);
        decreaseIndent();
        newLine(t);
        t.append('}');
        return t;
    }

    @Override
    public StringBuilder visit(UseStatement s, StringBuilder t) {
        newLine(t);
        printIndent(t);
        t.append("use ");
        s.expression.accept(this, t);
        return t;
    }


    private void newLine(StringBuilder t) {
        t.append(System.lineSeparator());
    }
    
    private void printIndent(StringBuilder sb) {
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
    }

    private void increaseIndent() {
        indent += 2;
    }

    private void decreaseIndent() {
        // Allow dedent to -2
        if (indent >= 0)
            indent -= 2;
    }
}
