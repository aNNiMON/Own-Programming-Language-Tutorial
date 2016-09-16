package com.annimon.ownlang.modules.math;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

/**
 *
 * @author aNNiMON
 */
public final class math implements Module {

    private static final DoubleFunction<NumberValue> doubleToNumber = NumberValue::of;

    public static void initConstants() {
        Variables.define("PI", NumberValue.of(Math.PI));
        Variables.define("E", NumberValue.of(Math.E));
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("abs", math::abs);
        Functions.set("acos", functionConvert(Math::acos));
        Functions.set("asin", functionConvert(Math::asin));
        Functions.set("atan", functionConvert(Math::atan));
        Functions.set("atan2", biFunctionConvert(Math::atan2));
        Functions.set("cbrt", functionConvert(Math::cbrt));
        Functions.set("ceil", functionConvert(Math::ceil));
        Functions.set("copySign", math::copySign);
        Functions.set("cos", functionConvert(Math::cos));
        Functions.set("cosh", functionConvert(Math::cosh));
        Functions.set("exp", functionConvert(Math::exp));
        Functions.set("expm1", functionConvert(Math::expm1));
        Functions.set("floor", functionConvert(Math::floor));
        Functions.set("getExponent", math::getExponent);
        Functions.set("hypot", biFunctionConvert(Math::hypot));
        Functions.set("IEEEremainder", biFunctionConvert(Math::IEEEremainder));
        Functions.set("log", functionConvert(Math::log));
        Functions.set("log1p", functionConvert(Math::log1p));
        Functions.set("log10", functionConvert(Math::log10));
        Functions.set("max", math::max);
        Functions.set("min", math::min);
        Functions.set("nextAfter", math::nextAfter);
        Functions.set("nextUp", functionConvert(Math::nextUp, Math::nextUp));
        Functions.set("nextDown", functionConvert(Math::nextDown, Math::nextDown));
        Functions.set("pow", biFunctionConvert(Math::pow));
        Functions.set("rint", functionConvert(Math::rint));
        Functions.set("round", math::round);
        Functions.set("signum", functionConvert(Math::signum, Math::signum));
        Functions.set("sin", functionConvert(Math::sin));
        Functions.set("sinh", functionConvert(Math::sinh));
        Functions.set("sqrt", functionConvert(Math::sqrt));
        Functions.set("tan", functionConvert(Math::tan));
        Functions.set("tanh", functionConvert(Math::tanh));
        Functions.set("toDegrees", functionConvert(Math::toDegrees));
        Functions.set("toRadians", functionConvert(Math::toRadians));
        Functions.set("ulp", functionConvert(Math::ulp, Math::ulp));
    }

    private static Value abs(Value... args) {
        Arguments.check(1, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Double) {
            return NumberValue.of(Math.abs((double) raw));
        }
        if (raw instanceof Float) {
            return NumberValue.of(Math.abs((float) raw));
        }
        if (raw instanceof Long) {
            return NumberValue.of(Math.abs((long) raw));
        }
        return NumberValue.of(Math.abs(args[0].asInt()));
    }

    private static Value copySign(Value... args) {
        Arguments.check(2, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Float) {
            return NumberValue.of(Math.copySign((float) raw, ((NumberValue) args[1]).asFloat()));
        }
        return NumberValue.of(Math.copySign(args[0].asNumber(), args[1].asNumber()));
    }

    private static Value getExponent(Value... args) {
        Arguments.check(1, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Float) {
            return NumberValue.of(Math.getExponent((float) raw));
        }
        return NumberValue.of(Math.getExponent(args[0].asNumber()));
    }

    private static Value max(Value... args) {
        Arguments.check(2, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Double) {
            return NumberValue.of(Math.max((double) raw, args[1].asNumber()));
        }
        if (raw instanceof Float) {
            return NumberValue.of(Math.max((float) raw, ((NumberValue) args[1]).asFloat()));
        }
        if (raw instanceof Long) {
            return NumberValue.of(Math.max((long) raw, ((NumberValue) args[1]).asLong()));
        }
        return NumberValue.of(Math.max(args[0].asInt(), args[1].asInt()));
    }

    private static Value min(Value... args) {
        Arguments.check(2, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Double) {
            return NumberValue.of(Math.min((double) raw, args[1].asNumber()));
        }
        if (raw instanceof Float) {
            return NumberValue.of(Math.min((float) raw, ((NumberValue) args[1]).asFloat()));
        }
        if (raw instanceof Long) {
            return NumberValue.of(Math.min((long) raw, ((NumberValue) args[1]).asLong()));
        }
        return NumberValue.of(Math.min(args[0].asInt(), args[1].asInt()));
    }

    private static Value nextAfter(Value... args) {
        Arguments.check(2, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Float) {
            return NumberValue.of(Math.nextAfter((float) raw, args[1].asNumber()));
        }
        return NumberValue.of(Math.nextAfter(args[0].asNumber(), args[1].asNumber()));
    }

    private static Value round(Value... args) {
        Arguments.check(1, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Float) {
            return NumberValue.of(Math.round((float) raw));
        }
        return NumberValue.of(Math.round(args[0].asNumber()));
    }


    private static Function functionConvert(DoubleUnaryOperator op) {
        return args -> {
            Arguments.check(1, args.length);
            return doubleToNumber.apply(op.applyAsDouble(args[0].asNumber()));
        };
    }

    private static Function functionConvert(DoubleUnaryOperator opDouble, UnaryOperator<Float> opFloat) {
        return args -> {
            Arguments.check(1, args.length);
            final Object raw = args[0].raw();
            if (raw instanceof Float) {
                return NumberValue.of(opFloat.apply((float) raw));
            }
            return NumberValue.of(opDouble.applyAsDouble(args[0].asNumber()));
        };
    }

    private static Function biFunctionConvert(DoubleBinaryOperator op) {
        return args -> {
            Arguments.check(2, args.length);
            return doubleToNumber.apply(op.applyAsDouble(args[0].asNumber(), args[1].asNumber()));
        };
    }
}
