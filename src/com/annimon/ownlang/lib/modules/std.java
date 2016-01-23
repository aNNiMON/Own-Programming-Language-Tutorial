package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.*;

/**
 *
 * @author aNNiMON
 */
public final class std implements Module {

    @Override
    public void init() {
        Functions.set("echo", new std_echo());
        Functions.set("readln", new std_readln());
        Functions.set("newarray", new std_newarray());
        Functions.set("sort", new std_sort());
        Functions.set("length", new std_length());
        Functions.set("rand", new std_rand());
        Functions.set("time", new std_time());
        Functions.set("sleep", new std_sleep());
        Functions.set("thread", new std_thread());
        
        // String
        Functions.set("sprintf", new std_sprintf());
        Functions.set("split", new std_split());
        Functions.set("join", new std_join());
        Functions.set("indexOf", new std_indexof());
        Functions.set("lastIndexOf", new std_lastindexof());
        Functions.set("charAt", new std_charat());
        Functions.set("toChar", new std_tochar());
        Functions.set("substring", new std_substring());
        Functions.set("toLowerCase", new std_tolowercase());
        Functions.set("toUpperCase", new std_touppercase());
        Functions.set("trim", new std_trim());
        Functions.set("replace", new std_replace());
        Functions.set("replaceAll", new std_replaceall());
        Functions.set("replaceFirst", new std_replacefirst());
    }
}
