package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import javax.swing.JScrollPane;

public class JScrollPaneValue extends JComponentValue {

    final JScrollPane scrollPane;

    public JScrollPaneValue(JScrollPane scrollPane) {
        super(10, scrollPane);
        this.scrollPane = scrollPane;
        init();
    }

    private void init() {
        set("getHorizontalScrollBarPolicy", voidToInt(scrollPane::getHorizontalScrollBarPolicy));
        set("getVerticalScrollBarPolicy", voidToInt(scrollPane::getVerticalScrollBarPolicy));
        set("isWheelScrollingEnabled", voidToBoolean(scrollPane::isWheelScrollingEnabled));
        set("setColumnHeaderView", new FunctionValue(this::setColumnHeaderView));
        set("setCorner", new FunctionValue(this::setCorner));
        set("setHorizontalScrollBarPolicy", intToVoid(scrollPane::setHorizontalScrollBarPolicy));
        set("setRowHeaderView", new FunctionValue(this::setRowHeaderView));
        set("setVerticalScrollBarPolicy", intToVoid(scrollPane::setVerticalScrollBarPolicy));
        set("setViewportView", new FunctionValue(this::setViewportView));
        set("setWheelScrollingEnabled", booleanToVoid(scrollPane::setWheelScrollingEnabled));
    }
    
    private Value setViewportView(Value[] args) {
        Arguments.check(1, args.length);
        scrollPane.setViewportView(((ComponentValue) args[0]).component);
        return NumberValue.ZERO;
    }
    
    private Value setRowHeaderView(Value[] args) {
        Arguments.check(1, args.length);
        scrollPane.setRowHeaderView(((ComponentValue) args[0]).component);
        return NumberValue.ZERO;
    }
    
    private Value setColumnHeaderView(Value[] args) {
        Arguments.check(1, args.length);
        scrollPane.setColumnHeaderView(((ComponentValue) args[0]).component);
        return NumberValue.ZERO;
    }
    
    private Value setCorner(Value[] args) {
        Arguments.check(2, args.length);
        scrollPane.setCorner(args[0].asString(), ((ComponentValue) args[1]).component);
        return NumberValue.ZERO;
    }
}