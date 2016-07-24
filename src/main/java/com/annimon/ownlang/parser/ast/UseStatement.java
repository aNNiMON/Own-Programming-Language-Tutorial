package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.modules.Module;
import java.lang.reflect.Method;

/**
 *
 * @author aNNiMON
 */
public final class UseStatement extends InterruptableNode implements Statement {

    private static final String PACKAGE = "com.annimon.ownlang.lib.modules.";
    private static final String INIT_CONSTANTS_METHOD = "initConstants";
    
    public final Expression expression;
    
    public UseStatement(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void execute() {
        super.interruptionCheck();
        try {
            final String moduleName = expression.eval().asString();
            final Module module = (Module) Class.forName(PACKAGE + moduleName).newInstance();
            module.init();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void loadConstants() {
        try {
            final String moduleName = expression.eval().asString();
            final Class<?> moduleClass = Class.forName(PACKAGE + moduleName);
            final Method method = moduleClass.getMethod(INIT_CONSTANTS_METHOD);
            if (method != null) {
                method.invoke(this);
            }
        } catch (Exception ex) {
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
