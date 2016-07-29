package com.annimon.ownlang.lib.modules.functions.forms;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import javax.swing.JTextField;

public class JTextFieldValue extends JComponentValue {

    private final JTextField textField;

    public JTextFieldValue(JTextField textField) {
        super(16, textField);
        this.textField = textField;
        init();
    }

    private void init() {
        set(new StringValue("onAction"), new FunctionValue(this::addActionListener));
        set(new StringValue("addActionListener"), new FunctionValue(this::addActionListener));
        set(new StringValue("getCaretPosition"), voidToInt(textField::getCaretPosition));
        set(new StringValue("getColumns"), voidToInt(textField::getColumns));
        set(new StringValue("getHorizontalAlignment"), voidToInt(textField::getHorizontalAlignment));
        set(new StringValue("getSelectionEnd"), voidToInt(textField::getSelectionEnd));
        set(new StringValue("getSelectionStart"), voidToInt(textField::getSelectionStart));
        set(new StringValue("getScrollOffset"), voidToInt(textField::getScrollOffset));
        set(new StringValue("getText"), voidToString(textField::getText));
        set(new StringValue("setCaretPosition"), intToVoid(textField::setCaretPosition));
        set(new StringValue("setColumns"), intToVoid(textField::setColumns));
        set(new StringValue("setHorizontalAlignment"), intToVoid(textField::setHorizontalAlignment));
        set(new StringValue("setScrollOffset"), intToVoid(textField::setScrollOffset));
        set(new StringValue("setSelectionEnd"), intToVoid(textField::setSelectionEnd));
        set(new StringValue("setSelectionStart"), intToVoid(textField::setSelectionStart));
        set(new StringValue("setText"), stringToVoid(textField::setText));
    }

    private Value addActionListener(Value... args) {
        Arguments.check(1, args.length);
        final int type = args[0].type();
        if (type != Types.FUNCTION) {
            throw new TypeException("Function expected, but found " + Types.typeToString(type));
        }
        final Function action = ((FunctionValue) args[0]).getValue();
        textField.addActionListener(e -> action.execute());
        return NumberValue.ZERO;
    }
}