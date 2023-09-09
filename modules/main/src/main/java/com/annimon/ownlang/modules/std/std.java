package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.Shared;
import com.annimon.ownlang.Version;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.util.Map;
import static java.util.Map.entry;

/**
 *
 * @author aNNiMON
 */
public final class std implements Module {

    @Override
    public Map<String, Value> constants() {
        MapValue ownlang = new MapValue(5);
        ownlang.set("PLATFORM", new StringValue("desktop"));
        ownlang.set("VERSION", new StringValue(Version.VERSION));
        ownlang.set("VERSION_MAJOR", NumberValue.of(Version.VERSION_MAJOR));
        ownlang.set("VERSION_MINOR", NumberValue.of(Version.VERSION_MINOR));
        ownlang.set("VERSION_PATCH", NumberValue.of(Version.VERSION_PATCH));

        return Map.of(
                "OwnLang", ownlang,
                "ARGS", ArrayValue.of(Shared.getOwnlangArgs())
        );
    }

    @Override
    public Map<String, Function> functions() {
        return Map.ofEntries(
                entry("echo", new std_echo()),
                entry("readln", new std_readln()),
                entry("length", new std_length()),
                entry("rand", new std_rand()),
                entry("time", new std_time()),
                entry("sleep", new std_sleep()),
                entry("thread", new std_thread()),
                entry("sync", new std_sync()),
                entry("try", new std_try()),
                entry("default", new std_default()),

                // Numbers
                entry("toHexString", NumberFunctions::toHexString),

                // String
                entry("getBytes", StringFunctions::getBytes),
                entry("sprintf", new std_sprintf()),
                entry("split", new std_split()),
                entry("indexOf", new std_indexof()),
                entry("lastIndexOf", new std_lastindexof()),
                entry("charAt", new std_charat()),
                entry("toChar", new std_tochar()),
                entry("substring", new std_substring()),
                entry("toLowerCase", new std_tolowercase()),
                entry("toUpperCase", new std_touppercase()),
                entry("trim", new std_trim()),
                entry("replace", new std_replace()),
                entry("replaceAll", new std_replaceall()),
                entry("replaceFirst", new std_replacefirst()),
                entry("parseInt", StringFunctions::parseInt),
                entry("parseLong", StringFunctions::parseLong),
                entry("stripMargin", StringFunctions::stripMargin),

                // Arrays and map,
                entry("newarray", new std_newarray()),
                entry("join", new std_join()),
                entry("sort", new std_sort()),
                entry("arrayCombine", new std_arrayCombine()),
                entry("arrayKeyExists", new std_arrayKeyExists()),
                entry("arrayKeys", new std_arrayKeys()),
                entry("arrayValues", new std_arrayValues()),
                entry("arraySplice", new std_arraySplice()),
                entry("range", new std_range()),
                entry("stringFromBytes", ArrayFunctions::stringFromBytes)
        );
    }
}
