package com.annimon.ownlang.lib;

import com.annimon.ownlang.parser.ast.Arguments;
import com.annimon.ownlang.parser.ast.Statement;

public class ClassMethod extends UserDefinedFunction {
    
    public final ClassInstanceValue classInstance;
    
    public ClassMethod(Arguments arguments, Statement body, ClassInstanceValue classInstance) {
        super(arguments, body);
        this.classInstance = classInstance;
    }
    
    @Override
    public Value execute(Value[] values) {
        Variables.push();
        Variables.define("this", classInstance.getThisMap());
        
        try {
            return super.execute(values);
        } finally {
            Variables.pop();
        }
    }
}
