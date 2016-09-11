package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

public final class std_thread implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkAtLeast(1, args.length);
        
        Function body;
        if (args[0].type() == Types.FUNCTION) {
            body = ((FunctionValue) args[0]).getValue();
        } else {
            body = Functions.get(args[0].asString());
        }
        
        // Сдвигаем аргументы
        final Value[] params = new Value[args.length - 1];
        if (params.length > 0) {
            System.arraycopy(args, 1, params, 0, params.length);
        }

        final Thread thread = new Thread(() -> body.execute(params));
        thread.setUncaughtExceptionHandler(Console::handleException);
        thread.start();
        return NumberValue.ZERO;
    }
}
