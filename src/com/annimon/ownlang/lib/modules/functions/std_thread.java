package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_thread implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length == 1) {
            // Создаём новый поток по имени функции
            new Thread(() -> {
                Functions.get(args[0].asString()).execute();
            }).start();
        }
        return NumberValue.ZERO;
    }
}