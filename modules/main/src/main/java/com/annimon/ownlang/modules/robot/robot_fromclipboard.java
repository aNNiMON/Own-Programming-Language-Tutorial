package com.annimon.ownlang.modules.robot;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;

public final class robot_fromclipboard implements Function {

    @Override
    public Value execute(Value[] args) {
        try {
            Object data = Toolkit.getDefaultToolkit().getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            return new StringValue(data.toString());
        } catch (Exception ex) {
            return StringValue.EMPTY;
        }
    }
}