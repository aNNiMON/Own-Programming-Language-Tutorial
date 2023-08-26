package com.annimon.ownlang.modules.json;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import org.json.JSONException;
import org.json.JSONTokener;

public final class json_decode implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        try {
            final String jsonRaw = args[0].asString();
            final Object root = new JSONTokener(jsonRaw).nextValue();
            return ValueUtils.toValue(root);
        } catch (JSONException ex) {
            throw new RuntimeException("Error while parsing json", ex);
        }
    }
}
