package com.annimon.ownlang.modules.forms;

import static com.annimon.ownlang.lib.Converters.*;
import javax.swing.JComponent;

public abstract class JComponentValue extends ContainerValue {

    final JComponent jComponent;

    public JComponentValue(int functionsCount, JComponent jComponent) {
        super(functionsCount + 14, jComponent);
        this.jComponent = jComponent;
        init();
    }

    private void init() {
        set("getAutoscrolls", voidToBoolean(jComponent::getAutoscrolls));
        set("setAutoscrolls", booleanToVoid(jComponent::setAutoscrolls));
        set("isDoubleBuffered", voidToBoolean(jComponent::isDoubleBuffered));
        set("setDoubleBuffered", booleanToVoid(jComponent::setDoubleBuffered));
        set("getInheritsPopupMenu", voidToBoolean(jComponent::getInheritsPopupMenu));
        set("setInheritsPopupMenu", booleanToVoid(jComponent::setInheritsPopupMenu));
        set("isOpaque", voidToBoolean(jComponent::isOpaque));
        set("setOpaque", booleanToVoid(jComponent::setOpaque));
        set("isRequestFocusEnabled", voidToBoolean(jComponent::isRequestFocusEnabled));
        set("setRequestFocusEnabled", booleanToVoid(jComponent::setRequestFocusEnabled));
        set("getVerifyInputWhenFocusTarget", voidToBoolean(jComponent::getVerifyInputWhenFocusTarget));
        set("setVerifyInputWhenFocusTarget", booleanToVoid(jComponent::setVerifyInputWhenFocusTarget));
        set("getToolTipText", voidToString(jComponent::getToolTipText));
        set("setToolTipText", stringToVoid(jComponent::setToolTipText));
    }
}
