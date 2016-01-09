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
    }
}
