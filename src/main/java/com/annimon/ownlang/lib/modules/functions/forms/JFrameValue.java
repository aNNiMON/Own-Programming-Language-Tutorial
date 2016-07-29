package com.annimon.ownlang.lib.modules.functions.forms;

import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.StringValue;
import javax.swing.JFrame;

public class JFrameValue extends ContainerValue {

    final JFrame frame;

    public JFrameValue(JFrame frame) {
        super(9, frame);
        this.frame = frame;
        init();
    }

    private void init() {
        set(new StringValue("dispose"), voidToVoid(frame::dispose));
        set(new StringValue("getTitle"), voidToString(frame::getTitle));
        set(new StringValue("getDefaultCloseOperation"), voidToInt(frame::getDefaultCloseOperation));
        set(new StringValue("pack"), voidToVoid(frame::pack));
        set(new StringValue("setAlwaysOnTop"), booleanOptToVoid(frame::setAlwaysOnTop));
        set(new StringValue("setDefaultCloseOperation"), intToVoid(frame::setDefaultCloseOperation));
        set(new StringValue("setLocationByPlatform"), booleanOptToVoid(frame::setLocationByPlatform));
        set(new StringValue("setResizable"), booleanOptToVoid(frame::setResizable));
        set(new StringValue("setTitle"), stringToVoid(frame::setTitle));
    }
}