package com.annimon.ownlang.modules.math;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.util.LinkedHashMap;
import java.util.Map;
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

    @Override
    public Map<String, Value> constants() {
        return Map.of(
                "PI", NumberValue.of(Math.PI),
                "E", NumberValue.of(Math.E)
        );
    }

    @Override
    public Map<String, Function> functions() {
        final var result = new LinkedHashMap<String, Function>(16);
        result.put("abs", math::abs);
        result.put("acos", functionConvert(Math::acos));
        result.put("asin", functionConvert(Math::asin));
        result.put("atan", functionConvert(Math::atan));
        result.put("atan2", biFunctionConvert(Math::atan2));
        result.put("cbrt", functionConvert(Math::cbrt));
        result.put("ceil", functionConvert(Math::ceil));
        result.put("copySign", math::copySign);
        result.put("cos", functionConvert(Math::cos));
        result.put("cosh", functionConvert(Math::cosh));
        result.put("exp", functionConvert(Math::exp));
        result.put("expm1", functionConvert(Math::expm1));
        result.put("floor", functionConvert(Math::floor));
        result.put("getExponent", math::getExponent);
        result.put("hypot", biFunctionConvert(Math::hypot));
        result.put("IEEEremainder", biFunctionConvert(Math::IEEEremainder));
        result.put("log", functionConvert(Math::log));
        result.put("log1p", functionConvert(Math::log1p));
        result.put("log10", functionConvert(Math::log10));
        result.put("max", math::max);
        result.put("min", math::min);
        result.put("nextAfter", math::nextAfter);
        result.put("nextUp", functionConvert(Math::nextUp, Math::nextUp));
        result.put("nextDown", functionConvert(Math::nextDown, Math::nextDown));
        result.put("pow", biFunctionConvert(Math::pow));
        result.put("rint", functionConvert(Math::rint));
        result.put("round", math::round);
        result.put("signum", functionConvert(Math::signum, Math::signum));
        result.put("sin", functionConvert(Math::sin));
        result.put("sinh", functionConvert(Math::sinh));
        result.put("sqrt", functionConvert(Math::sqrt));
        result.put("tan", functionConvert(Math::tan));
        result.put("tanh", functionConvert(Math::tanh));
        result.put("toDegrees", functionConvert(Math::toDegrees));
        result.put("toRadians", functionConvert(Math::toRadians));
        result.put("ulp", functionConvert(Math::ulp, Math::ulp));
        return result;
    }

    private static Value abs(Value[] args) {
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

    private static Value copySign(Value[] args) {
        Arguments.check(2, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Float) {
            return NumberValue.of(Math.copySign((float) raw, ((NumberValue) args[1]).asFloat()));
        }
        return NumberValue.of(Math.copySign(args[0].asNumber(), args[1].asNumber()));
    }

    private static Value getExponent(Value[] args) {
        Arguments.check(1, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Float) {
            return NumberValue.of(Math.getExponent((float) raw));
        }
        return NumberValue.of(Math.getExponent(args[0].asNumber()));
    }

    private static Value max(Value[] args) {
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

    private static Value min(Value[] args) {
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

    private static Value nextAfter(Value[] args) {
        Arguments.check(2, args.length);
        final Object raw = args[0].raw();
        if (raw instanceof Float) {
            return NumberValue.of(Math.nextAfter((float) raw, args[1].asNumber()));
        }
        return NumberValue.of(Math.nextAfter(args[0].asNumber(), args[1].asNumber()));
    }

    private static Value round(Value[] args) {
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
