package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import javax.swing.AbstractButton;

public class AbstractButtonValue extends JComponentValue {

    final AbstractButton abstractButton;

    public AbstractButtonValue(int functionsCount, AbstractButton abstractButton) {
        super(functionsCount + 2, abstractButton);
        this.abstractButton = abstractButton;
        init();
    }

    private void init() {
        set("onClick", new FunctionValue(this::addActionListener));
        set("addActionListener", new FunctionValue(this::addActionListener));
        set("onChange", new FunctionValue(this::addChangeListener));
        set("addChangeListener", new FunctionValue(this::addChangeListener));
        set("doClick", intOptToVoid(abstractButton::doClick, abstractButton::doClick));
        set("getActionCommand", voidToString(abstractButton::getActionCommand));
        set("getDisplayedMnemonicIndex", voidToInt(abstractButton::getDisplayedMnemonicIndex));
        set("getHideActionText", voidToBoolean(abstractButton::getHideActionText));
        set("getHorizontalAlignment", voidToInt(abstractButton::getHorizontalAlignment));
        set("getHorizontalTextPosition", voidToInt(abstractButton::getHorizontalTextPosition));
        set("getIconTextGap", voidToInt(abstractButton::getIconTextGap));
        set("getText", voidToString(abstractButton::getText));
        set("getVerticalAlignment", voidToInt(abstractButton::getVerticalAlignment));
        set("getVerticalTextPosition", voidToInt(abstractButton::getVerticalTextPosition));
        set("isSelected", voidToBoolean(abstractButton::isSelected));
        set("setActionCommand", stringToVoid(abstractButton::setActionCommand));
        set("setHorizontalAlignment", intToVoid(abstractButton::setHorizontalAlignment));
        set("setHorizontalTextPosition", intToVoid(abstractButton::setHorizontalTextPosition));
        set("setSelected", booleanToVoid(abstractButton::setSelected));
        set("setText", stringToVoid(abstractButton::setText));
        set("setVerticalAlignment", intToVoid(abstractButton::setVerticalAlignment));
        set("setVerticalTextPosition", intToVoid(abstractButton::setVerticalTextPosition));
    }

    private Value addActionListener(Value[] args) {
        Arguments.check(1, args.length);
        final Function action = ValueUtils.consumeFunction(args[0], 0);
        abstractButton.addActionListener(e -> action.execute());
        return NumberValue.ZERO;
    }
    
    private Value addChangeListener(Value[] args) {
        Arguments.check(1, args.length);
        final Function action = ValueUtils.consumeFunction(args[0], 0);
        abstractButton.addChangeListener(e -> action.execute());
        return NumberValue.ZERO;
    }
}