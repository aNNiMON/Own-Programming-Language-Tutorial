package com.annimon.ownlang.lib.modules.functions.forms;

import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.StringValue;
import javax.swing.JLabel;

public class JLabelValue extends JComponentValue {

    final JLabel label;

    public JLabelValue(JLabel label) {
        super(17, label);
        this.label = label;
        init();
    }

    private void init() {
        set(new StringValue("getDisplayedMnemonic"), voidToInt(label::getDisplayedMnemonic));
        set(new StringValue("getDisplayedMnemonicIndex"), voidToInt(label::getDisplayedMnemonicIndex));
        set(new StringValue("getHorizontalAlignment"), voidToInt(label::getHorizontalAlignment));
        set(new StringValue("getHorizontalTextPosition"), voidToInt(label::getHorizontalTextPosition));
        set(new StringValue("getIconTextGap"), voidToInt(label::getIconTextGap));
        set(new StringValue("getVerticalAlignment"), voidToInt(label::getVerticalAlignment));
        set(new StringValue("getVerticalTextPosition"), voidToInt(label::getVerticalTextPosition));

        set(new StringValue("getText"), voidToString(label::getText));
        set(new StringValue("setDisplayedMnemonic"), intToVoid(label::setDisplayedMnemonic));
        set(new StringValue("setDisplayedMnemonicIndex"), intToVoid(label::setDisplayedMnemonicIndex));
        set(new StringValue("setHorizontalAlignment"), intToVoid(label::setHorizontalAlignment));
        set(new StringValue("setHorizontalTextPosition"), intToVoid(label::setHorizontalTextPosition));
        set(new StringValue("setIconTextGap"), intToVoid(label::setIconTextGap));
        set(new StringValue("setVerticalAlignment"), intToVoid(label::setVerticalAlignment));
        set(new StringValue("setVerticalTextPosition"), intToVoid(label::setVerticalTextPosition));
        set(new StringValue("setText"), stringToVoid(label::setText));
    }
}