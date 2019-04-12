package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.io.UnsupportedEncodingException;

public final class ArrayFunctions {
    
    private ArrayFunctions() { }
    
    static StringValue stringFromBytes(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected at first argument");
        }
        final byte[] bytes = ValueUtils.toByteArray((ArrayValue) args[0]);
        final String charset = (args.length == 2) ? args[1].asString() : "UTF-8";
        try {
            return new StringValue(new String(bytes, charset));
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }
}
