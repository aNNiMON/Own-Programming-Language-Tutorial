package com.annimon.ownlang.lib.modules.functions.forms;

import static com.annimon.ownlang.lib.Converters.*;
import javax.swing.JComponent;

public abstract class JComponentValue extends ContainerValue {

    final JComponent jComponent;

    public JComponentValue(int functionsCount, JComponent jComponent) {
        super(functionsCount + 2, jComponent);
        this.jComponent = jComponent;
        init();
    }

    private void init() {
        set("getToolTipText", voidToString(jComponent::getToolTipText));
        set("setToolTipText", stringToVoid(jComponent::setToolTipText));
    }
}
