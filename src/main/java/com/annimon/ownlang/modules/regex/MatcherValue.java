package com.annimon.ownlang.modules.regex;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.regex.Matcher;

public class MatcherValue extends MapValue {

    private final Matcher value;

    public MatcherValue(Matcher value) {
        super(22);
        this.value = value;
        init();
    }

    private void init() {
        set("start", this::start);
        set("end", this::end);
        set("find", this::find);
        set("group", this::group);
        set("pattern", this::pattern);
        set("region", this::region);
        set("replaceFirst", this::replaceFirst);
        set("replaceAll", this::replaceAll);
        set("replaceCallback", this::replaceCallback);
        set("reset", this::reset);
        set("usePattern", this::usePattern);
        set("useAnchoringBounds", this::useAnchoringBounds);
        set("hasAnchoringBounds", Converters.voidToBoolean(value::hasAnchoringBounds));
        set("useTransparentBounds", this::useTransparentBounds);
        set("hasTransparentBounds", Converters.voidToBoolean(value::hasTransparentBounds));
        set("hitEnd", Converters.voidToBoolean(value::hitEnd));
        set("lookingAt", Converters.voidToBoolean(value::lookingAt));
        set("matches", Converters.voidToBoolean(value::matches));
        set("groupCount", Converters.voidToInt(value::groupCount));
        set("regionStart", Converters.voidToInt(value::regionStart));
        set("regionEnd", Converters.voidToInt(value::regionEnd));
    }

    private Value start(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        final int result;
        if (args.length == 0) {
            result = value.start();
        } else {
            final Value arg = args[0];
            if (arg.type() == Types.NUMBER) {
                result = value.start(arg.asInt());
            } else {
                result = value.start(arg.asString());
            }
        }
        return NumberValue.of(result);
    }

    private Value end(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        final int result;
        if (args.length == 0) {
            result = value.end();
        } else {
            final Value arg = args[0];
            if (arg.type() == Types.NUMBER) {
                result = value.end(arg.asInt());
            } else {
                result = value.end(arg.asString());
            }
        }
        return NumberValue.of(result);
    }

    private Value find(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        final boolean result;
        if (args.length == 0) {
            result = value.find();
        } else {
            result = value.find(args[0].asInt());
        }
        return NumberValue.fromBoolean(result);
    }

    private Value group(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        final String result;
        if (args.length == 0) {
            result = value.group();
        } else {
            final Value arg = args[0];
            if (arg.type() == Types.NUMBER) {
                result = value.group(arg.asInt());
            } else {
                result = value.group(arg.asString());
            }
        }
        return new StringValue(result);
    }

    private Value pattern(Value[] args) {
        return new PatternValue(value.pattern());
    }

    private Value region(Value[] args) {
        Arguments.check(2, args.length);
        value.region(args[0].asInt(), args[1].asInt());
        return this;
    }

    private Value replaceFirst(Value[] args) {
        Arguments.check(1, args.length);
        return new StringValue(value.replaceFirst(args[0].asString()));
    }

    private Value replaceAll(Value[] args) {
        Arguments.check(1, args.length);
        return new StringValue(value.replaceAll(args[0].asString()));
    }

    static StringValue replaceCallback(MatcherValue matcherValue, Function callback) {
        final Matcher matcher = matcherValue.value;
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement = callback.execute(matcherValue).asString();
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return new StringValue(sb.toString());
    }

    private Value replaceCallback(Value[] args) {
        Arguments.check(1, args.length);
        if (args[0].type() != Types.FUNCTION) {
            throw new TypeException(args[0].toString() + " is not a function");
        }
        return replaceCallback(this, ((FunctionValue) args[0]).getValue());
    }

    private Value reset(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        if (args.length == 0) {
            value.reset();
        } else {
            value.reset(args[0].asString());
        }
        return this;
    }

    private Value useAnchoringBounds(Value[] args) {
        Arguments.check(1, args.length);
        value.useAnchoringBounds(args[0].asInt() != 0);
        return this;
    }

    private Value useTransparentBounds(Value[] args) {
        Arguments.check(1, args.length);
        value.useTransparentBounds(args[0].asInt() != 0);
        return this;
    }

    private Value usePattern(Value[] args) {
        Arguments.check(1, args.length);
        final Value arg = args[0];
        if (arg.type() == Types.MAP && (arg instanceof PatternValue)) {
            value.usePattern(((PatternValue) arg).getValue());
        } else {
            throw new TypeException("Pattern value expected");
        }
        return this;
    }
}
