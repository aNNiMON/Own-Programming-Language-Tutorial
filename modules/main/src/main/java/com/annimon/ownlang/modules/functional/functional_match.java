package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

final class functional_match {

    enum MatchType { ALL, ANY, NONE }

    static Function match(MatchType matchType) {
        return args -> {
            Arguments.check(2, args.length);
            final Value container = args[0];
            if (container.type() != Types.ARRAY) {
                throw new TypeException("Invalid first argument. Array expected");
            }
            final Function predicate = ValueUtils.consumeFunction(args[1], 1);
            return switch (matchType) {
                case ALL -> allMatch((ArrayValue) container, predicate);
                case ANY -> anyMatch((ArrayValue) container, predicate);
                case NONE -> noneMatch((ArrayValue) container, predicate);
            };
        };
    }

    private static NumberValue allMatch(ArrayValue array, Function predicate) {
        for (Value value : array) {
            if (predicate.execute(value) == NumberValue.ZERO) {
                return NumberValue.ZERO;
            }
        }
        return NumberValue.ONE;
    }

    private static NumberValue anyMatch(ArrayValue array, Function predicate) {
        for (Value value : array) {
            if (predicate.execute(value) != NumberValue.ZERO) {
                return NumberValue.ONE;
            }
        }
        return NumberValue.ZERO;
    }

    private static NumberValue noneMatch(ArrayValue array, Function predicate) {
        for (Value value : array) {
            if (predicate.execute(value) != NumberValue.ZERO) {
                return NumberValue.ZERO;
            }
        }
        return NumberValue.ONE;
    }
}
