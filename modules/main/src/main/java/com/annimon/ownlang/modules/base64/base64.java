package com.annimon.ownlang.modules.base64;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class base64 implements Module {

    private static final int TYPE_URL_SAFE = 8;

    public static void initConstants() {
        Variables.define("BASE64_URL_SAFE", NumberValue.of(TYPE_URL_SAFE));
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("base64encode", this::base64encode);
        Functions.set("base64decode", this::base64decode);
        Functions.set("base64encodeToString", this::base64encodeToString);
    }

    private Value base64encode(Value... args) {
        Arguments.checkOrOr(1, 2, args.length);
        return ArrayValue.of(getEncoder(args).encode(getInputToEncode(args)));
    }

    private Value base64encodeToString(Value... args) {
        Arguments.checkOrOr(1, 2, args.length);
        return new StringValue(getEncoder(args).encodeToString(getInputToEncode(args)));
    }

    private Value base64decode(Value... args) {
        Arguments.checkOrOr(1, 2, args.length);
        final Base64.Decoder decoder = getDecoder(args);
        final byte[] result;
        if (args[0].type() == Types.ARRAY) {
            result = decoder.decode(ValueUtils.toByteArray((ArrayValue) args[0]));
        } else {
            result = decoder.decode(args[0].asString());
        }
        return ArrayValue.of(result);
    }


    private byte[] getInputToEncode(Value[] args) {
        byte[] input;
        if (args[0].type() == Types.ARRAY) {
            input = ValueUtils.toByteArray((ArrayValue) args[0]);
        } else {
            try {
                input = args[0].asString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                input = args[0].asString().getBytes();
            }
        }
        return input;
    }

    private Base64.Encoder getEncoder(Value[] args) {
        if (args.length == 2 && args[1].asInt() == TYPE_URL_SAFE) {
            return Base64.getUrlEncoder();
        }
        return Base64.getEncoder();
    }

    private Base64.Decoder getDecoder(Value[] args) {
        if (args.length == 2 && args[1].asInt() == TYPE_URL_SAFE) {
            return Base64.getUrlDecoder();
        }
        return Base64.getDecoder();
    }
}
