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
        ScopeHandler.setConstant("PI", NumberValue.of(Math.PI));
        ScopeHandler.setConstant("E", NumberValue.of(Math.E));
    }

    @Override
    public void init() {
        initConstants();
        ScopeHandler.setFunction("abs", math::abs);
        ScopeHandler.setFunction("acos", functionConvert(Math::acos));
        ScopeHandler.setFunction("asin", functionConvert(Math::asin));
        ScopeHandler.setFunction("atan", functionConvert(Math::atan));
        ScopeHandler.setFunction("atan2", biFunctionConvert(Math::atan2));
        ScopeHandler.setFunction("cbrt", functionConvert(Math::cbrt));
        ScopeHandler.setFunction("ceil", functionConvert(Math::ceil));
        ScopeHandler.setFunction("copySign", math::copySign);
        ScopeHandler.setFunction("cos", functionConvert(Math::cos));
        ScopeHandler.setFunction("cosh", functionConvert(Math::cosh));
        ScopeHandler.setFunction("exp", functionConvert(Math::exp));
        ScopeHandler.setFunction("expm1", functionConvert(Math::expm1));
        ScopeHandler.setFunction("floor", functionConvert(Math::floor));
        ScopeHandler.setFunction("getExponent", math::getExponent);
        ScopeHandler.setFunction("hypot", biFunctionConvert(Math::hypot));
        ScopeHandler.setFunction("IEEEremainder", biFunctionConvert(Math::IEEEremainder));
        ScopeHandler.setFunction("log", functionConvert(Math::log));
        ScopeHandler.setFunction("log1p", functionConvert(Math::log1p));
        ScopeHandler.setFunction("log10", functionConvert(Math::log10));
        ScopeHandler.setFunction("max", math::max);
        ScopeHandler.setFunction("min", math::min);
        ScopeHandler.setFunction("nextAfter", math::nextAfter);
        ScopeHandler.setFunction("nextUp", functionConvert(Math::nextUp, Math::nextUp));
        ScopeHandler.setFunction("nextDown", functionConvert(Math::nextDown, Math::nextDown));
        ScopeHandler.setFunction("pow", biFunctionConvert(Math::pow));
        ScopeHandler.setFunction("rint", functionConvert(Math::rint));
        ScopeHandler.setFunction("round", math::round);
        ScopeHandler.setFunction("signum", functionConvert(Math::signum, Math::signum));
        ScopeHandler.setFunction("sin", functionConvert(Math::sin));
        ScopeHandler.setFunction("sinh", functionConvert(Math::sinh));
        ScopeHandler.setFunction("sqrt", functionConvert(Math::sqrt));
        ScopeHandler.setFunction("tan", functionConvert(Math::tan));
        ScopeHandler.setFunction("tanh", functionConvert(Math::tanh));
        ScopeHandler.setFunction("toDegrees", functionConvert(Math::toDegrees));
        ScopeHandler.setFunction("toRadians", functionConvert(Math::toRadians));
        ScopeHandler.setFunction("ulp", functionConvert(Math::ulp, Math::ulp));
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
