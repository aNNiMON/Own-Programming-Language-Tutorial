package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class std_sync implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);

        final BlockingQueue<Value> queue = new LinkedBlockingQueue<>(2);
        final Function synchronizer = (sArgs) -> {
            try {
                queue.put(sArgs[0]);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return NumberValue.ZERO;
        };
        final Function callback = ValueUtils.consumeFunction(args[0], 0);
        callback.execute(new FunctionValue(synchronizer));

        try {
            return queue.take();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
    
}
