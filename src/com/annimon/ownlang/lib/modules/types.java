package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.*;

/**
 *
 * @author aNNiMON
 */
public final class types implements Module {

    @Override
    public void init() {
        Variables.set("OBJECT", new NumberValue(Types.OBJECT));
        Variables.set("NUMBER", new NumberValue(Types.NUMBER));
        Variables.set("STRING", new NumberValue(Types.STRING));
        Variables.set("ARRAY", new NumberValue(Types.ARRAY));
        Variables.set("MAP", new NumberValue(Types.MAP));
        Variables.set("FUNCTION", new NumberValue(Types.FUNCTION));
        
        Functions.set("typeof", args -> new NumberValue(args[0].type()));
        Functions.set("string", args -> new StringValue(args[0].asString()));
        Functions.set("number", args -> new NumberValue(args[0].asNumber()));
        
        Functions.set("byte", args -> new NumberValue((byte)args[0].asInt()));
        Functions.set("short", args -> new NumberValue((short)args[0].asInt()));
        Functions.set("int", args -> new NumberValue(args[0].asInt()));
        Functions.set("long", args -> new NumberValue((long)args[0].asNumber()));
        Functions.set("float", args -> new NumberValue((float)args[0].asNumber()));
        Functions.set("double", args -> new NumberValue(args[0].asNumber()));
    }
}
