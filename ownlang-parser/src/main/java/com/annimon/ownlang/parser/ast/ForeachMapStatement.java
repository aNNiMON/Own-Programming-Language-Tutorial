package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class ForeachMapStatement extends InterruptableNode implements Statement {
    
    public final String key, value;
    public final Expression container;
    public final Statement body;

    public ForeachMapStatement(String key, String value, Expression container, Statement body) {
        this.key = key;
        this.value = value;
        this.container = container;
        this.body = body;
    }

    @Override
    public void execute() {
        super.interruptionCheck();
        // TODO removing without checking shadowing is dangerous
        final Value previousVariableValue1 = ScopeHandler.getVariable(key);
        final Value previousVariableValue2 = ScopeHandler.getVariable(value);

        final Value containerValue = container.eval();
        switch (containerValue.type()) {
            case Types.STRING:
                iterateString(containerValue.asString());
                break;
            case Types.ARRAY:
                iterateArray((ArrayValue) containerValue);
                break;
            case Types.MAP:
                iterateMap((MapValue) containerValue);
                break;
            default:
                throw new TypeException("Cannot iterate " + Types.typeToString(containerValue.type()) + " as key, value pair");
        }

        // Restore variables
        if (previousVariableValue1 != null) {
            ScopeHandler.setVariable(key, previousVariableValue1);
        } else {
            ScopeHandler.removeVariable(key);
        }
        if (previousVariableValue2 != null) {
            ScopeHandler.setVariable(value, previousVariableValue2);
        } else {
            ScopeHandler.removeVariable(value);
        }
    }

    private void iterateString(String str) {
        for (char ch : str.toCharArray()) {
            ScopeHandler.setVariable(key, new StringValue(String.valueOf(ch)));
            ScopeHandler.setVariable(value, NumberValue.of(ch));
            try {
                body.execute();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
        }
    }

    private void iterateArray(ArrayValue containerValue) {
        int index = 0;
        for (Value v : containerValue) {
            ScopeHandler.setVariable(key, v);
            ScopeHandler.setVariable(value, NumberValue.of(index++));
            try {
                body.execute();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
        }
    }

    private void iterateMap(MapValue containerValue) {
        for (Map.Entry<Value, Value> entry : containerValue) {
            ScopeHandler.setVariable(key, entry.getKey());
            ScopeHandler.setVariable(value, entry.getValue());
            try {
                body.execute();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
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
        return String.format("for %s, %s : %s %s", key, value, container, body);
    }
}
