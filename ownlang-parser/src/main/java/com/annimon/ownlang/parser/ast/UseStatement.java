package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.modules.Module;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 *
 * @author aNNiMON
 */
public final class UseStatement extends InterruptableNode implements Statement {

    private static final String PACKAGE = "com.annimon.ownlang.modules.%s.%s";
    private static final String INIT_CONSTANTS_METHOD = "initConstants";
    
    public final Collection<String> modules;
    
    public UseStatement(Collection<String> modules) {
        this.modules = modules;
    }
    
    @Override
    public void execute() {
        super.interruptionCheck();
        for (String module : modules) {
            loadModule(module);
        }
    }

    private void loadModule(String name) {
        try {
            final Module module = (Module) Class.forName(String.format(PACKAGE, name, name))
                    .getDeclaredConstructor()
                    .newInstance();
            module.init();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load module " + name, ex);
        }
    }

    public void loadConstants() {
        for (String module : modules) {
            loadConstants(module);
        }
    }

    private void loadConstants(String moduleName) {
        try {
            final Class<?> moduleClass = Class.forName(String.format(PACKAGE, moduleName, moduleName));
            final Method method = moduleClass.getMethod(INIT_CONSTANTS_METHOD);
            method.invoke(this);
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
        return "use " + String.join(", ", modules);
    }
}
