package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import javax.swing.JTextField;
import static com.annimon.ownlang.lib.ValueUtils.consumeFunction;

public class JTextFieldValue extends JTextComponentValue {

    private final JTextField textField;

    public JTextFieldValue(JTextField textField) {
        super(10, textField);
        this.textField = textField;
        init();
    }

    private void init() {
        set("onAction", new FunctionValue(this::addActionListener));
        set("addActionListener", new FunctionValue(this::addActionListener));
        set("getColumns", voidToInt(textField::getColumns));
        set("getHorizontalAlignment", voidToInt(textField::getHorizontalAlignment));
        set("getScrollOffset", voidToInt(textField::getScrollOffset));
        set("postActionEvent", voidToVoid(textField::postActionEvent));
        set("setActionCommand", stringToVoid(textField::setActionCommand));
        set("setColumns", intToVoid(textField::setColumns));
        set("setHorizontalAlignment", intToVoid(textField::setHorizontalAlignment));
        set("setScrollOffset", intToVoid(textField::setScrollOffset));
    }

    private Value addActionListener(Value[] args) {
        Arguments.check(1, args.length);
        Function action = consumeFunction(args[0], 1);
        textField.addActionListener(e -> action.execute());
        return NumberValue.ZERO;
    }
}