package com.annimon.ownlang.modules;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Value;
import java.util.Map;

/**
 * Main interface for modules
 * @author aNNiMON
 */
public interface Module {

    Map<String, Value> constants();

    Map<String, Function> functions();
}
