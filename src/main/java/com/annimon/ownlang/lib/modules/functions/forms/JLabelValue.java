package com.annimon.ownlang.lib.modules.functions.forms;

import static com.annimon.ownlang.lib.Converters.*;
import javax.swing.JLabel;

public class JLabelValue extends JComponentValue {

    final JLabel label;

    public JLabelValue(JLabel label) {
        super(17, label);
        this.label = label;
        init();
    }

    private void init() {
        set("getDisplayedMnemonic", voidToInt(label::getDisplayedMnemonic));
        set("getDisplayedMnemonicIndex", voidToInt(label::getDisplayedMnemonicIndex));
        set("getHorizontalAlignment", voidToInt(label::getHorizontalAlignment));
        set("getHorizontalTextPosition", voidToInt(label::getHorizontalTextPosition));
        set("getIconTextGap", voidToInt(label::getIconTextGap));
        set("getVerticalAlignment", voidToInt(label::getVerticalAlignment));
        set("getVerticalTextPosition", voidToInt(label::getVerticalTextPosition));

        set("getText", voidToString(label::getText));
        set("setDisplayedMnemonic", intToVoid(label::setDisplayedMnemonic));
        set("setDisplayedMnemonicIndex", intToVoid(label::setDisplayedMnemonicIndex));
        set("setHorizontalAlignment", intToVoid(label::setHorizontalAlignment));
        set("setHorizontalTextPosition", intToVoid(label::setHorizontalTextPosition));
        set("setIconTextGap", intToVoid(label::setIconTextGap));
        set("setVerticalAlignment", intToVoid(label::setVerticalAlignment));
        set("setVerticalTextPosition", intToVoid(label::setVerticalTextPosition));
        set("setText", stringToVoid(label::setText));
    }
}