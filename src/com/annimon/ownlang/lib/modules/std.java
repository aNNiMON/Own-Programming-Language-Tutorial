package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.*;
import java.util.Random;

/**
 *
 * @author aNNiMON
 */
public final class std implements Module {

    @Override
    public void init() {
        Functions.set("echo", args -> {
            for (Value arg : args) {
                System.out.print(arg.asString());
                System.out.print(" ");
            }
            System.out.println();
            return NumberValue.ZERO;
        });
        Functions.set("newarray", new Function() {

            @Override
            public Value execute(Value... args) {
                return createArray(args, 0);
            }
            
            private ArrayValue createArray(Value[] args, int index) {
                final int size = (int) args[index].asNumber();
                final int last = args.length - 1;
                ArrayValue array = new ArrayValue(size);
                if (index == last) {
                    for (int i = 0; i < size; i++) {
                        array.set(i, NumberValue.ZERO);
                    }
                } else if (index < last) {
                    for (int i = 0; i < size; i++) {
                        array.set(i, createArray(args, index + 1));
                    }
                }
                return array;
            }
        });
        Functions.set("rand", new Rand());
        Functions.set("sleep", args -> {
            if (args.length == 1) {
                try {
                    Thread.sleep((long) args[0].asNumber());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            return NumberValue.ZERO;
        });
        Functions.set("thread", args -> {
            if (args.length == 1) {
                // Создаём новый поток по имени функции
                new Thread(() -> {
                    Functions.get(args[0].asString()).execute();
                }).start();
            }
            return NumberValue.ZERO;
        });
    }

    private static class Rand implements Function {
    
        private static final Random RND = new Random();

        @Override
        public Value execute(Value... args) {
            if (args.length == 0) return new NumberValue(RND.nextDouble());
            
            int from = 0;
            int to = 100;
            if (args.length == 1) {
                to = (int) args[0].asNumber();
            } else if (args.length == 2) {
                from = (int) args[0].asNumber();
                to = (int) args[1].asNumber();
            }
            return new NumberValue(RND.nextInt(to - from) + from);
        }
    }
}
