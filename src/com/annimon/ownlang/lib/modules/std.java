package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.std_echo;
import com.annimon.ownlang.lib.modules.functions.std_foreach;
import com.annimon.ownlang.lib.modules.functions.std_newarray;
import com.annimon.ownlang.lib.modules.functions.std_rand;
import com.annimon.ownlang.lib.modules.functions.std_sleep;
import com.annimon.ownlang.lib.modules.functions.std_thread;
import java.util.Random;

/**
 *
 * @author aNNiMON
 */
public final class std implements Module {

    @Override
    public void init() {
        Functions.set("echo", new std_echo());
        Functions.set("newarray", new std_newarray());
        Functions.set("rand", new std_rand());
        Functions.set("sleep", new std_sleep());
        Functions.set("thread", new std_thread());
    }
}
