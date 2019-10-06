package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import javax.swing.JProgressBar;

public class JProgressBarValue extends JComponentValue {

    final JProgressBar progressBar;

    public JProgressBarValue(JProgressBar progressBar) {
        super(19, progressBar);
        this.progressBar = progressBar;
        init();
    }

    private void init() {
        set("onChange", new FunctionValue(this::addChangeListener));
        set("addChangeListener", new FunctionValue(this::addChangeListener));
        set("getMinimum", voidToInt(progressBar::getMinimum));
        set("getMaximum", voidToInt(progressBar::getMaximum));
        set("getOrientation", voidToInt(progressBar::getOrientation));
        set("getValue", voidToInt(progressBar::getValue));
        set("isBorderPainted", voidToBoolean(progressBar::isBorderPainted));
        set("isIndeterminate", voidToBoolean(progressBar::isIndeterminate));
        set("isStringPainted", voidToBoolean(progressBar::isStringPainted));
        set("getString", voidToString(progressBar::getString));
        set("getPercentComplete", voidToDouble(progressBar::getPercentComplete));
        
        set("setMinimum", intToVoid(progressBar::setMinimum));
        set("setMaximum", intToVoid(progressBar::setMaximum));
        set("setBorderPainted", booleanToVoid(progressBar::setBorderPainted));
        set("setIndeterminate", booleanToVoid(progressBar::setIndeterminate));
        set("setOrientation", intToVoid(progressBar::setOrientation));
        set("setStringPainted", booleanToVoid(progressBar::setStringPainted));
        set("setString", stringToVoid(progressBar::setString));
        set("setValue", intToVoid(progressBar::setValue));
    }
    
    private Value addChangeListener(Value[] args) {
        Arguments.check(1, args.length);
        final Function action = ValueUtils.consumeFunction(args[0], 0);
        progressBar.addChangeListener(e -> action.execute());
        return NumberValue.ZERO;
    }
}