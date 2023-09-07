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
        try (final var ignored = ScopeHandler.closeableScope()) {
            final Value containerValue = container.eval();
            switch (containerValue.type()) {
                case Types.STRING -> iterateString(containerValue.asString());
                case Types.ARRAY -> iterateArray((ArrayValue) containerValue);
                case Types.MAP -> iterateMap((MapValue) containerValue);
                default -> throw new TypeException("Cannot iterate " + Types.typeToString(containerValue.type()) + " as key, value pair");
            }
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
