package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

public final class std_thread implements Function {

    @Override
    public Value execute(Value... args) {
        // Создаём новый поток и передаём параметры, если есть.
        // Функция может передаваться как напрямую, так и по имени
        if (args.length == 0) throw new RuntimeException("At least one arg expected");
        
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
        
        new Thread(() -> body.execute(params)).start();
        return NumberValue.ZERO;
    }
}