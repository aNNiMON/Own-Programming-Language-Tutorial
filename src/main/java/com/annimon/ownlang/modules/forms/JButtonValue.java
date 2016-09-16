package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import javax.swing.JButton;

public class JButtonValue extends JComponentValue {

    final JButton button;

    public JButtonValue(JButton button) {
        super(2, button);
        this.button = button;
        init();
    }

    private void init() {
        set("onClick", new FunctionValue(this::addActionListener));
        set("addActionListener", new FunctionValue(this::addActionListener));
    }

    private Value addActionListener(Value... args) {
        Arguments.check(1, args.length);
        final int type = args[0].type();
        if (type != Types.FUNCTION) {
            throw new TypeException("Function expected, but found " + Types.typeToString(type));
        }
        final Function action = ((FunctionValue) args[0]).getValue();
        button.addActionListener(e -> action.execute());
        return NumberValue.ZERO;
    }
}