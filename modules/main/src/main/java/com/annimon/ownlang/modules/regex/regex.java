package com.annimon.ownlang.modules.regex;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.util.regex.Pattern;

public final class regex implements Module {

    public static void initConstants() {
        MapValue map = new MapValue(20);
        map.set("UNIX_LINES", NumberValue.of(Pattern.UNIX_LINES));
        map.set("I", NumberValue.of(Pattern.CASE_INSENSITIVE));
        map.set("CASE_INSENSITIVE", NumberValue.of(Pattern.CASE_INSENSITIVE));
        map.set("COMMENTS", NumberValue.of(Pattern.COMMENTS));
        map.set("M", NumberValue.of(Pattern.MULTILINE));
        map.set("MULTILINE", NumberValue.of(Pattern.MULTILINE));
        map.set("LITERAL", NumberValue.of(Pattern.LITERAL));
        map.set("S", NumberValue.of(Pattern.DOTALL));
        map.set("DOTALL", NumberValue.of(Pattern.DOTALL));
        map.set("UNICODE_CASE", NumberValue.of(Pattern.UNICODE_CASE));
        map.set("CANON_EQ", NumberValue.of(Pattern.CANON_EQ));
        map.set("U", NumberValue.of(Pattern.UNICODE_CHARACTER_CLASS));
        map.set("UNICODE_CHARACTER_CLASS", NumberValue.of(Pattern.UNICODE_CHARACTER_CLASS));

        map.set("quote", args -> {
            Arguments.check(1, args.length);
            return new StringValue(Pattern.quote(args[0].asString()));
        });
        map.set("matches", args -> {
            Arguments.check(2, args.length);
            return NumberValue.fromBoolean(Pattern.matches(args[0].asString(), args[1].asString()));
        });
        map.set("split", args -> {
            Arguments.checkOrOr(2, 3, args.length);
            final Pattern pattern = Pattern.compile(args[0].asString());
            final int limit = (args.length == 3) ? args[2].asInt() : 0;
            return ArrayValue.of(pattern.split(args[1].asString(), limit));
        });
        map.set("compile", regex::compile);
        ScopeHandler.setConstant("Pattern", map);
    }

    @Override
    public void init() {
        initConstants();
        ScopeHandler.setFunction("regex", regex::compile);
    }

    private static Value compile(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final int flags = (args.length == 2) ? args[1].asInt() : 0;
        return new PatternValue(Pattern.compile(args[0].asString(), flags));
    }
}
