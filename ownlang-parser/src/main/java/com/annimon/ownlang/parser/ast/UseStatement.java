package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.ModuleLoader;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.modules.Module;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class UseStatement extends InterruptableNode implements Statement {
    public final Collection<String> modules;
    
    public UseStatement(Collection<String> modules) {
        this.modules = modules;
    }
    
    @Override
    public Value eval() {
        super.interruptionCheck();
        for (String module : modules) {
            ModuleLoader.loadAndUse(module);
        }
        return NumberValue.ZERO;
    }

    public Map<String, Value> loadConstants() {
        final var result = new LinkedHashMap<String, Value>();
        for (String moduleName : modules) {
            final Module module = ModuleLoader.load(moduleName);
            result.putAll(module.constants());
        }
        return result;
    }

    public Map<String, Function> loadFunctions() {
        final var result = new LinkedHashMap<String, Function>();
        for (String moduleName : modules) {
            final Module module = ModuleLoader.load(moduleName);
            result.putAll(module.functions());
        }
        return result;
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
