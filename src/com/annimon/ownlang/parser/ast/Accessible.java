package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;

public interface Accessible {

    Value get();
    
    Value set(Value value);
}
