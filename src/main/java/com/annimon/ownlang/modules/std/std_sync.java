package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class std_sync implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        if (args[0].type() != Types.FUNCTION) {
            throw new TypeException(args[0].toString() + " is not a function");
        }

        final BlockingQueue<Value> queue = new LinkedBlockingQueue<>(2);
        final Function synchronizer = (sArgs) -> {
            try {
                queue.put(sArgs[0]);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return NumberValue.ZERO;
        };
        final Function callback = ((FunctionValue) args[0]).getValue();
        callback.execute(new FunctionValue(synchronizer));

        try {
            return queue.take();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
    
}