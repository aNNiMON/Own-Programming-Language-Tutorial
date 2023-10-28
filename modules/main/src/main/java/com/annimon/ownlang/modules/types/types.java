package com.annimon.ownlang.modules.types;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.util.Map;
import static java.util.Map.entry;

/**
 *
 * @author aNNiMON
 */
public final class types implements Module {

    @Override
    public Map<String, Value> constants() {
        return Map.ofEntries(
                entry("OBJECT", NumberValue.of(Types.OBJECT)),
                entry("NUMBER", NumberValue.of(Types.NUMBER)),
                entry("STRING", NumberValue.of(Types.STRING)),
                entry("ARRAY", NumberValue.of(Types.ARRAY)),
                entry("MAP", NumberValue.of(Types.MAP)),
                entry("FUNCTION", NumberValue.of(Types.FUNCTION)),
                entry("CLASS", NumberValue.of(Types.CLASS))
        );
    }

    @Override
    public Map<String, Function> functions() {
        return Map.ofEntries(
                entry("typeof", args -> NumberValue.of(args[0].type())),
                entry("string", args -> new StringValue(args[0].asString())),
                entry("number", args -> NumberValue.of(args[0].asNumber())),

                entry("byte", args -> NumberValue.of((byte)args[0].asInt())),
                entry("short", args -> NumberValue.of((short)args[0].asInt())),
                entry("int", args -> NumberValue.of(args[0].asInt())),
                entry("long", args -> NumberValue.of((long)args[0].asNumber())),
                entry("float", args -> NumberValue.of((float)args[0].asNumber())),
                entry("double", args -> NumberValue.of(args[0].asNumber()))
        );
    }
}
