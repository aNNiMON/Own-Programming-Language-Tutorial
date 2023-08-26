package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class JTextComponentValue extends JComponentValue {

    private final JTextComponent textComponent;

    public JTextComponentValue(int functionsCount, JTextComponent textComponent) {
        super(functionsCount + 21, textComponent);
        this.textComponent = textComponent;
        init();
    }

    private void init() {
        set("addCaretListener", this::addCaretListener);
        set("addDocumentListener", this::addDocumentListener);
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
    
    private Value addCaretListener(Value[] args) {
        Arguments.check(1, args.length);
        final Function action = ValueUtils.consumeFunction(args[0], 0);
        textComponent.addCaretListener((CaretEvent e) -> {
            final MapValue map = new MapValue(2);
            map.set("getDot", NumberValue.of(e.getDot()));
            map.set("getMark", NumberValue.of(e.getMark()));
            action.execute(map);
        });
        return NumberValue.ZERO;
    }
    
    private Value addDocumentListener(Value[] args) {
        Arguments.check(1, args.length);
        final Function action = ValueUtils.consumeFunction(args[0], 0);
        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleDocumentEvent(DocumentEvent.EventType.INSERT, e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleDocumentEvent(DocumentEvent.EventType.REMOVE, e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleDocumentEvent(DocumentEvent.EventType.CHANGE, e);
            }
            
            private void handleDocumentEvent(DocumentEvent.EventType type, final DocumentEvent e) {
                final MapValue map = new MapValue(3);
                map.set("getLength", NumberValue.of(e.getLength()));
                map.set("getOffset", NumberValue.of(e.getOffset()));
                map.set("getType", new StringValue(e.getType().toString()));
                action.execute(new StringValue(type.toString()), map);
            }
        });
        return NumberValue.ZERO;
    }
}