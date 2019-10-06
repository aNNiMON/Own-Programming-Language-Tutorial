package com.annimon.ownlang.modules.forms;

import static com.annimon.ownlang.lib.Converters.*;
import javax.swing.text.JTextComponent;

public class JTextComponentValue extends JComponentValue {

    private final JTextComponent textComponent;

    public JTextComponentValue(int functionsCount, JTextComponent textComponent) {
        super(functionsCount + 20, textComponent);
        this.textComponent = textComponent;
        init();
    }

    private void init() {
        set("copy", voidToVoid(textComponent::copy));
        set("cut", voidToVoid(textComponent::cut));
        set("getCaretPosition", voidToInt(textComponent::getCaretPosition));
        set("getDragEnabled", voidToBoolean(textComponent::getDragEnabled));
        set("getSelectedText", voidToString(textComponent::getSelectedText));
        set("getSelectionStart", voidToInt(textComponent::getSelectionStart));
        set("getSelectionEnd", voidToInt(textComponent::getSelectionEnd));
        set("getText", voidToString(textComponent::getText));
        set("isEditable", voidToBoolean(textComponent::isEditable));
        set("moveCaretPosition", intToVoid(textComponent::moveCaretPosition));
        set("paste", voidToVoid(textComponent::paste));
        set("replaceSelection", stringToVoid(textComponent::replaceSelection));
        set("select", int2ToVoid(textComponent::select));
        set("selectAll", voidToVoid(textComponent::selectAll));
        set("setCaretPosition", intToVoid(textComponent::setCaretPosition));
        set("setDragEnabled", booleanToVoid(textComponent::setDragEnabled));
        set("setEditable", booleanToVoid(textComponent::setEditable));
        set("setSelectionStart", intToVoid(textComponent::setSelectionStart));
        set("setSelectionEnd", intToVoid(textComponent::setSelectionEnd));
        set("setText", stringToVoid(textComponent::setText));
    }
}