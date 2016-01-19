package com.annimon.ownlang.lib;

/**
 *
 * @author aNNiMON
 */
public interface Value extends Comparable<Value> {
    
    double asNumber();
    
    String asString();
    
    int type();
}
