package com.annimon.ownlang.lib.modules.functions.forms;

import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.StringValue;
import javax.swing.JComponent;

public abstract class JComponentValue extends ContainerValue {

    final JComponent jComponent;

    public JComponentValue(int functionsCount, JComponent jComponent) {
        super(functionsCount + 2, jComponent);
        this.jComponent = jComponent;
        init();
    }

    private void init() {
        set(new StringValue("getToolTipText"), voidToString(jComponent::getToolTipText));
        set(new StringValue("setToolTipText"), stringToVoid(jComponent::setToolTipText));
    }
}
