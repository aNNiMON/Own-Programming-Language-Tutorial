package com.annimon.ownlang.modules.types;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;

/**
 *
 * @author aNNiMON
 */
public final class types implements Module {

    public static void initConstants() {
        ScopeHandler.setConstant("OBJECT", NumberValue.of(Types.OBJECT));
        ScopeHandler.setConstant("NUMBER", NumberValue.of(Types.NUMBER));
        ScopeHandler.setConstant("STRING", NumberValue.of(Types.STRING));
        ScopeHandler.setConstant("ARRAY", NumberValue.of(Types.ARRAY));
        ScopeHandler.setConstant("MAP", NumberValue.of(Types.MAP));
        ScopeHandler.setConstant("FUNCTION", NumberValue.of(Types.FUNCTION));
    }

    @Override
    public void init() {
        initConstants();
        ScopeHandler.setFunction("typeof", args -> NumberValue.of(args[0].type()));
        ScopeHandler.setFunction("string", args -> new StringValue(args[0].asString()));
        ScopeHandler.setFunction("number", args -> NumberValue.of(args[0].asNumber()));
        
        ScopeHandler.setFunction("byte", args -> NumberValue.of((byte)args[0].asInt()));
        ScopeHandler.setFunction("short", args -> NumberValue.of((short)args[0].asInt()));
        ScopeHandler.setFunction("int", args -> NumberValue.of(args[0].asInt()));
        ScopeHandler.setFunction("long", args -> NumberValue.of((long)args[0].asNumber()));
        ScopeHandler.setFunction("float", args -> NumberValue.of((float)args[0].asNumber()));
        ScopeHandler.setFunction("double", args -> NumberValue.of(args[0].asNumber()));
    }
}
