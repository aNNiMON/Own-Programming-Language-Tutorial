package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.Shared;
import com.annimon.ownlang.Version;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;

/**
 *
 * @author aNNiMON
 */
public final class std implements Module {

    public static void initConstants() {
        MapValue ownlang = new MapValue(5);
        ownlang.set("PLATFORM", new StringValue("desktop"));
        ownlang.set("VERSION", new StringValue(Version.VERSION));
        ownlang.set("VERSION_MAJOR", NumberValue.of(Version.VERSION_MAJOR));
        ownlang.set("VERSION_MINOR", NumberValue.of(Version.VERSION_MINOR));
        ownlang.set("VERSION_PATCH", NumberValue.of(Version.VERSION_PATCH));
        ScopeHandler.setConstant("OwnLang", ownlang);
    }

    @Override
    public void init() {
        initConstants();
        ScopeHandler.setConstant("ARGS", ArrayValue.of(Shared.getOwnlangArgs())); // is not constant
        ScopeHandler.setFunction("echo", new std_echo());
        ScopeHandler.setFunction("readln", new std_readln());
        ScopeHandler.setFunction("length", new std_length());
        ScopeHandler.setFunction("rand", new std_rand());
        ScopeHandler.setFunction("time", new std_time());
        ScopeHandler.setFunction("sleep", new std_sleep());
        ScopeHandler.setFunction("thread", new std_thread());
        ScopeHandler.setFunction("sync", new std_sync());
        ScopeHandler.setFunction("try", new std_try());
        ScopeHandler.setFunction("default", new std_default());

        // Numbers
        ScopeHandler.setFunction("toHexString", NumberFunctions::toHexString);

        // String
        ScopeHandler.setFunction("getBytes", StringFunctions::getBytes);
        ScopeHandler.setFunction("sprintf", new std_sprintf());
        ScopeHandler.setFunction("split", new std_split());
        ScopeHandler.setFunction("indexOf", new std_indexof());
        ScopeHandler.setFunction("lastIndexOf", new std_lastindexof());
        ScopeHandler.setFunction("charAt", new std_charat());
        ScopeHandler.setFunction("toChar", new std_tochar());
        ScopeHandler.setFunction("substring", new std_substring());
        ScopeHandler.setFunction("toLowerCase", new std_tolowercase());
        ScopeHandler.setFunction("toUpperCase", new std_touppercase());
        ScopeHandler.setFunction("trim", new std_trim());
        ScopeHandler.setFunction("replace", new std_replace());
        ScopeHandler.setFunction("replaceAll", new std_replaceall());
        ScopeHandler.setFunction("replaceFirst", new std_replacefirst());
        ScopeHandler.setFunction("parseInt", StringFunctions::parseInt);
        ScopeHandler.setFunction("parseLong", StringFunctions::parseLong);
        ScopeHandler.setFunction("stripMargin", StringFunctions::stripMargin);

        // Arrays and maps
        ScopeHandler.setFunction("newarray", new std_newarray());
        ScopeHandler.setFunction("join", new std_join());
        ScopeHandler.setFunction("sort", new std_sort());
        ScopeHandler.setFunction("arrayCombine", new std_arrayCombine());
        ScopeHandler.setFunction("arrayKeyExists", new std_arrayKeyExists());
        ScopeHandler.setFunction("arrayKeys", new std_arrayKeys());
        ScopeHandler.setFunction("arrayValues", new std_arrayValues());
        ScopeHandler.setFunction("arraySplice", new std_arraySplice());
        ScopeHandler.setFunction("range", new std_range());
        ScopeHandler.setFunction("stringFromBytes", ArrayFunctions::stringFromBytes);
    }
}
