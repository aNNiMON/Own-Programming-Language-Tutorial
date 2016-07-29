package com.annimon.ownlang.lib.modules.functions.forms;

import static com.annimon.ownlang.lib.Converters.*;
import javax.swing.JFrame;

public class JFrameValue extends ContainerValue {

    final JFrame frame;

    public JFrameValue(JFrame frame) {
        super(9, frame);
        this.frame = frame;
        init();
    }

    private void init() {
        set("dispose", voidToVoid(frame::dispose));
        set("getTitle", voidToString(frame::getTitle));
        set("getDefaultCloseOperation", voidToInt(frame::getDefaultCloseOperation));
        set("pack", voidToVoid(frame::pack));
        set("setAlwaysOnTop", booleanOptToVoid(frame::setAlwaysOnTop));
        set("setDefaultCloseOperation", intToVoid(frame::setDefaultCloseOperation));
        set("setLocationByPlatform", booleanOptToVoid(frame::setLocationByPlatform));
        set("setResizable", booleanOptToVoid(frame::setResizable));
        set("setTitle", stringToVoid(frame::setTitle));
    }
}