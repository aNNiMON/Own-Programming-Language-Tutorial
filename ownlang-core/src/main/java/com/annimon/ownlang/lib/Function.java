package com.annimon.ownlang.lib;

/**
 *
 * @author aNNiMON
 */
public interface Function {

    Value execute(Value... args);

    default int getArgsCount() {
        return 0;
    }
}
