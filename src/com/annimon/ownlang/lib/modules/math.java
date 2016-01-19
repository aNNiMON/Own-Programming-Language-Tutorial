package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author aNNiMON
 */
public final class math implements Module {
    
    private static final DoubleFunction<NumberValue> doubleToNumber = v -> new NumberValue(v);

    @Override
    public void init() {
        Functions.set("abs", functionConvert(Math::abs));
        Functions.set("acos", functionConvert(Math::acos));
        Functions.set("asin", functionConvert(Math::asin));
        Functions.set("atan", functionConvert(Math::atan));
        Functions.set("atan2", biFunctionConvert(Math::atan2));
        Functions.set("cbrt", functionConvert(Math::cbrt));
        Functions.set("ceil", functionConvert(Math::ceil));
        Functions.set("copySign", biFunctionConvert(Math::copySign));
        Functions.set("cos", functionConvert(Math::cos));
        Functions.set("cosh", functionConvert(Math::cosh));
        Functions.set("exp", functionConvert(Math::exp));
        Functions.set("expm1", functionConvert(Math::expm1));
        Functions.set("floor", functionConvert(Math::floor));
        Functions.set("getExponent", functionConvert(Math::getExponent));
        Functions.set("hypot", biFunctionConvert(Math::hypot));
        Functions.set("IEEEremainder", biFunctionConvert(Math::IEEEremainder));
        Functions.set("log", functionConvert(Math::log));
        Functions.set("log1p", functionConvert(Math::log1p));
        Functions.set("log10", functionConvert(Math::log10));
        Functions.set("max", biFunctionConvert(Math::max));
        Functions.set("min", biFunctionConvert(Math::min));
        Functions.set("nextAfter", biFunctionConvert(Math::nextAfter));
        Functions.set("nextUp", functionConvert(Math::nextUp));
        Functions.set("pow", biFunctionConvert(Math::pow));
        Functions.set("rint", functionConvert(Math::rint));
        Functions.set("round", functionConvert(Math::round));
        Functions.set("signum", functionConvert(Math::signum));
        Functions.set("sin", functionConvert(Math::sin));
        Functions.set("sinh", functionConvert(Math::sinh));
        Functions.set("sqrt", functionConvert(Math::sqrt));
        Functions.set("tan", functionConvert(Math::tan));
        Functions.set("tanh", functionConvert(Math::tanh));
        Functions.set("toDegrees", functionConvert(Math::toDegrees));
        Functions.set("toRadians", functionConvert(Math::toRadians));
        Functions.set("ulp", functionConvert(Math::ulp));

        Variables.set("PI", new NumberValue(Math.PI));
        Variables.set("E", new NumberValue(Math.E));
    }
    
    private static Function functionConvert(DoubleUnaryOperator op) {
        return args -> {
            if (args.length != 1) throw new ArgumentsMismatchException("One arg expected");
            return doubleToNumber.apply(op.applyAsDouble(args[0].asNumber()));
        };
    }
    
    private static Function biFunctionConvert(DoubleBinaryOperator op) {
        return args -> {
            if (args.length != 2) throw new ArgumentsMismatchException("Two args expected");
            return doubleToNumber.apply(op.applyAsDouble(args[0].asNumber(), args[1].asNumber()));
        };
    }
}
