package com.annimon.ownlang.modules.robot;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public final class robot_toclipboard implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(args[0].asString()), null);
        return NumberValue.ZERO;
    }
}