package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.modules.Module;
import java.lang.reflect.Method;

/**
 *
 * @author aNNiMON
 */
public final class UseStatement extends InterruptableNode implements Statement {

    private static final String PACKAGE = "com.annimon.ownlang.modules.%s.%s";
    private static final String INIT_CONSTANTS_METHOD = "initConstants";
    
    public final Expression expression;
    
    public UseStatement(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void execute() {
        super.interruptionCheck();
        final Value value = expression.eval();
        switch (value.type()) {
            case Types.ARRAY:
                for (Value module : ((ArrayValue) value)) {
                    loadModule(module.asString());
                }
                break;
            case Types.STRING:
                loadModule(value.asString());
                break;
            default:
                throw typeException(value);
        }
    }

    private void loadModule(String name) {
        try {
            final Module module = (Module) Class.forName(String.format(PACKAGE, name, name)).newInstance();
            module.init();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load module " + name, ex);
        }
    }

    public void loadConstants() {
        if (expression instanceof ArrayExpression) {
            ArrayExpression ae = (ArrayExpression) expression;
            for (Expression expr : ae.elements) {
                loadConstants(expr.eval().asString());
            }
        }
        if (expression instanceof ValueExpression) {
            ValueExpression ve = (ValueExpression) expression;
            loadConstants(ve.value.asString());
        }
    }

    private TypeException typeException(Value value) {
        return new TypeException("Array or string required in 'use' statement, " +
                "got " + Types.typeToString(value.type()) + " " + value);
    }

    private void loadConstants(String moduleName) {
        try {
            final Class<?> moduleClass = Class.forName(String.format(PACKAGE, moduleName, moduleName));
            final Method method = moduleClass.getMethod(INIT_CONSTANTS_METHOD);
            if (method != null) {
                method.invoke(this);
            }
        } catch (Exception ex) {
            // ignore
        }
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
        return "use " + expression;
    }
}
