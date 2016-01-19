package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.parser.ast.ReturnStatement;
import com.annimon.ownlang.parser.ast.Statement;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class UserDefinedFunction implements Function {
    
    private final List<String> argNames;
    private final Statement body;
    
    public UserDefinedFunction(List<String> argNames, Statement body) {
        this.argNames = argNames;
        this.body = body;
    }
    
    public int getArgsCount() {
        return argNames.size();
    }
    
    public String getArgsName(int index) {
        if (index < 0 || index >= getArgsCount()) return "";
        return argNames.get(index);
    }

    @Override
    public Value execute(Value... values) {
        final int size = values.length;
        if (size != getArgsCount()) throw new ArgumentsMismatchException("Arguments count mismatch");

        try {
            Variables.push();
            for (int i = 0; i < size; i++) {
                Variables.set(getArgsName(i), values[i]);
            }
            body.execute();
            return NumberValue.ZERO;
        } catch (ReturnStatement rt) {
            return rt.getResult();
        } finally {
            Variables.pop();
        }
    }

    @Override
    public String toString() {
        return String.format("function %s %s", argNames.toString(), body.toString());
    }
}
