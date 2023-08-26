package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.Arrays;

public final class functional_stream implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkAtLeast(1, args.length);

        if (args.length > 1) {
            return new StreamValue(new ArrayValue(args));
        }

        final Value value = args[0];
        switch (value.type()) {
            case Types.MAP:
                return new StreamValue(((MapValue) value).toPairs());
            case Types.ARRAY:
                return new StreamValue((ArrayValue) value);
            default:
                throw new TypeException("Invalid argument. Array or map expected");
        }
    }

    private static class StreamValue extends MapValue {

        private final ArrayValue container;

        public StreamValue(ArrayValue container) {
            super(16);
            this.container = container;
            init();
        }

        private void init() {
            set("filter", wrapIntermediate(new functional_filter(false)));
            set("map", wrapIntermediate(new functional_map()));
            set("flatMap", wrapIntermediate(new functional_flatmap()));
            set("sorted", this::sorted);
            set("sortBy", wrapIntermediate(new functional_sortby()));
            set("takeWhile", wrapIntermediate(new functional_filter(true)));
            set("dropWhile", wrapIntermediate(new functional_dropwhile()));
            set("peek", wrapIntermediate(new functional_foreach()));
            set("skip", this::skip);
            set("limit", this::limit);
            set("custom", this::custom);

            set("reduce", wrapTerminal(new functional_reduce()));
            set("forEach", wrapTerminal(new functional_foreach()));
            set("toArray", args -> container);
            set("joining", container::joinToString);
            set("count", args -> NumberValue.of(container.size()));
        }

        private Value skip(Value[] args) {
            Arguments.check(1, args.length);

            final int skipCount = args[0].asInt();
            final int size = container.size();

            if (skipCount <= 0) return this;
            if (skipCount >= size) {
                return new StreamValue(new ArrayValue(0));
            }

            final Value[] result = new Value[size - skipCount];
            System.arraycopy(container.getCopyElements(), skipCount, result, 0, result.length);
            return new StreamValue(new ArrayValue(result));
        }

        private Value limit(Value[] args) {
            Arguments.check(1, args.length);

            final int limitCount = args[0].asInt();
            final int size = container.size();

            if (limitCount >= size) return this;
            if (limitCount <= 0) {
                return new StreamValue(new ArrayValue(0));
            }

            final Value[] result = new Value[limitCount];
            System.arraycopy(container.getCopyElements(), 0, result, 0, limitCount);
            return new StreamValue(new ArrayValue(result));
        }

        private Value sorted(Value[] args) {
            Arguments.checkOrOr(0, 1, args.length);
            final Value[] elements = container.getCopyElements();

            switch (args.length) {
                case 0:
                    Arrays.sort(elements);
                    break;
                case 1:
                    final Function comparator = ValueUtils.consumeFunction(args[0], 0);
                    Arrays.sort(elements, (o1, o2) -> comparator.execute(o1, o2).asInt());
                    break;
                default:
                    throw new ArgumentsMismatchException("Wrong number of arguments");
            }

            return new StreamValue(new ArrayValue(elements));
        }

        private Value custom(Value[] args) {
            Arguments.check(1, args.length);
            final Function f = ValueUtils.consumeFunction(args[0], 0);
            final Value result = f.execute(container);
            if (result.type() == Types.ARRAY) {
                return new StreamValue((ArrayValue) result);
            }
            return result;
        }

        private FunctionValue wrapIntermediate(Function f) {
            return wrap(f, true);
        }

        private FunctionValue wrapTerminal(Function f) {
            return wrap(f, false);
        }

        private FunctionValue wrap(Function f, boolean intermediate) {
            return new FunctionValue(args -> {
                final Value[] newArgs = new Value[args.length + 1];
                System.arraycopy(args, 0, newArgs, 1, args.length);
                newArgs[0] = container;
                final Value result = f.execute(newArgs);
                if (intermediate && result.type() == Types.ARRAY) {
                    return new StreamValue((ArrayValue) result);
                }
                return result;
            });
        }
    }
}
