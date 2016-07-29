package com.annimon.ownlang.lib.modules.functions.forms;

import com.annimon.ownlang.lib.MapValue;
import java.awt.LayoutManager;

public class LayoutManagerValue extends MapValue {

    final LayoutManager layout;

    public LayoutManagerValue(LayoutManager layout) {
        super(0);
        this.layout = layout;
    }
}