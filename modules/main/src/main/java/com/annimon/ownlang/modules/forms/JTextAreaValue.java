package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class JTextAreaValue extends JTextComponentValue {

    private final JTextArea textArea;

    public JTextAreaValue(JTextArea textArea) {
        super(18, textArea);
        this.textArea = textArea;
        init();
    }

    private void init() {
        set("append", stringToVoid(textArea::append));
        set("getColumns", voidToInt(textArea::getColumns));
        set("getLineCount", voidToInt(textArea::getLineCount));
        set("getLineStartOffset", offsetFunction(textArea::getLineStartOffset));
        set("getLineEndOffset", offsetFunction(textArea::getLineEndOffset));
        set("getLineOfOffset", offsetFunction(textArea::getLineOfOffset));
        set("getLineWrap", voidToBoolean(textArea::getLineWrap));
        set("getWrapStyleWord", voidToBoolean(textArea::getWrapStyleWord));
        set("getRows", voidToInt(textArea::getRows));
        set("getColumns", voidToInt(textArea::getColumns));
        set("getTabSize", voidToInt(textArea::getTabSize));
        set("insert", this::insert);
        set("setRows", intToVoid(textArea::setRows));
        set("setColumns", intToVoid(textArea::setColumns));
        set("setTabSize", intToVoid(textArea::setTabSize));
        set("setLineWrap", booleanToVoid(textArea::setLineWrap));
        set("setWrapStyleWord", booleanToVoid(textArea::setWrapStyleWord));
    }
    
    private Value insert(Value[] args) {
        Arguments.check(2, args.length);
        textArea.insert(args[0].asString(), args[1].asInt());
        return NumberValue.ZERO;
    }
    
    private interface OffsetFunction {
        int accept(int line) throws BadLocationException;
    }
    
    private FunctionValue offsetFunction(OffsetFunction f) {
        return new FunctionValue(args -> {
            Arguments.check(1, args.length);
            try {
                int result = f.accept(args[0].asInt());
                return NumberValue.of(result);
            } catch (BadLocationException ex) {
                throw new OwnLangRuntimeException(ex);
            }
        });
    }
}