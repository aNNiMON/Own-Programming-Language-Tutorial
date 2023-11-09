package com.annimon.ownlang.modules.server;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import io.javalin.Javalin;
import java.util.Map;
import static java.util.Map.entry;

public final class server implements Module {

    @Override
    public Map<String, Value> constants() {
        return Map.of();
    }

    @Override
    public Map<String, Function> functions() {
        return Map.ofEntries(
                entry("newServer", this::newServer),
                entry("serve", this::serve)
        );
    }

    private Value newServer(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        if (args.length == 0) {
            return new ServerValue(Javalin.create());
        } else {
            final Function configConsumer = ValueUtils.consumeFunction(args[0], 0);
            return new ServerValue(Javalin.create(config -> configConsumer.execute()));
        }
    }

    private Value serve(Value[] args) {
        // get path, port
        // javalin start()
        return NumberValue.ZERO;

    }
}
