package com.annimon.ownlang.modules.forms;

import static com.annimon.ownlang.lib.Converters.*;
import javax.swing.JFrame;

public class JFrameValue extends WindowValue {

    final JFrame frame;

    public JFrameValue(JFrame frame) {
        super(9, frame);
        this.frame = frame;
        init();
    }

    private void init() {
        set("getTitle", voidToString(frame::getTitle));
        set("getResizable", voidToBoolean(frame::isResizable));
        set("getDefaultCloseOperation", voidToInt(frame::getDefaultCloseOperation));
        set("setDefaultCloseOperation", intToVoid(frame::setDefaultCloseOperation));
        set("setResizable", booleanOptToVoid(frame::setResizable));
        set("setTitle", stringToVoid(frame::setTitle));
    }
}