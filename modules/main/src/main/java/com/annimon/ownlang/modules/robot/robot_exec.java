package com.annimon.ownlang.modules.robot;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

public final class robot_exec implements Function {
    
    public enum Mode { EXEC, EXEC_AND_WAIT }
    
    private final Mode mode;

    public robot_exec(Mode mode) {
        this.mode = mode;
    }

    @Override
    public Value execute(Value[] args) {
        Arguments.checkAtLeast(1, args.length);
        
        try {
            final Process process;
            if (args.length > 1) {
                process = Runtime.getRuntime().exec(toStringArray(args));
            } else switch (args[0].type()) {
                case Types.ARRAY:
                    final ArrayValue array = (ArrayValue) args[0];
                    process = Runtime.getRuntime().exec(toStringArray(array.getCopyElements()));
                    break;
                
                default:
                    process = Runtime.getRuntime().exec(args[0].asString());
            }
            
            switch (mode) {
                case EXEC_AND_WAIT:
                    return NumberValue.of(process.waitFor());
                case EXEC:
                default:
                    return NumberValue.ZERO;
            }
        } catch (Exception ex) {
            return NumberValue.ZERO;
        }
    }
    
    private static String[] toStringArray(Value[] values) {
        final String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].asString();
        }
        return strings;
    }
}
