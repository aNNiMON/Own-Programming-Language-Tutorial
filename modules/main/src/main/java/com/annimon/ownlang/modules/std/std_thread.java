package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.*;

final class std_thread implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkAtLeast(1, args.length);
        
        Function body;
        if (args[0].type() == Types.FUNCTION) {
            body = ((FunctionValue) args[0]).getValue();
        } else {
            body = ScopeHandler.getFunction(args[0].asString());
        }
        
        // Shift arguments
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
