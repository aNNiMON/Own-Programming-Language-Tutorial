package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.annotations.ConstantInitializer;
import com.annimon.ownlang.lib.*;

/**
 *
 * @author aNNiMON
 */
@ConstantInitializer
public final class types implements Module {

    public static void initConstants() {
        Variables.define("OBJECT", NumberValue.of(Types.OBJECT));
        Variables.define("NUMBER", NumberValue.of(Types.NUMBER));
        Variables.define("STRING", NumberValue.of(Types.STRING));
        Variables.define("ARRAY", NumberValue.of(Types.ARRAY));
        Variables.define("MAP", NumberValue.of(Types.MAP));
        Variables.define("FUNCTION", NumberValue.of(Types.FUNCTION));
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("typeof", args -> NumberValue.of(args[0].type()));
        Functions.set("string", args -> new StringValue(args[0].asString()));
        Functions.set("number", args -> NumberValue.of(args[0].asNumber()));
        
        Functions.set("byte", args -> NumberValue.of((byte)args[0].asInt()));
        Functions.set("short", args -> NumberValue.of((short)args[0].asInt()));
        Functions.set("int", args -> NumberValue.of(args[0].asInt()));
        Functions.set("long", args -> NumberValue.of((long)args[0].asNumber()));
        Functions.set("float", args -> NumberValue.of((float)args[0].asNumber()));
        Functions.set("double", args -> NumberValue.of(args[0].asNumber()));
    }
}
