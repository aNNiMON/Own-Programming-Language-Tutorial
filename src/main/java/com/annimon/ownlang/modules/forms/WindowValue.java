package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowValue extends ContainerValue {

    private final Window window;

    public WindowValue(int functionsCount, Window window) {
        super(functionsCount + 18, window);
        this.window = window;
        init();
    }

    private void init() {
        set("addWindowListener", this::addWindowListener);
        set("dispose", voidToVoid(window::dispose));
        set("isActive", voidToBoolean(window::isActive));
        set("isAlwaysOnTop", voidToBoolean(window::isAlwaysOnTop));
        set("isAlwaysOnTopSupported", voidToBoolean(window::isAlwaysOnTopSupported));
        set("isAutoRequestFocus", voidToBoolean(window::isAutoRequestFocus));
        set("isFocusableWindow", voidToBoolean(window::isFocusableWindow));
        set("isFocused", voidToBoolean(window::isFocused));
        set("isLocationByPlatform", voidToBoolean(window::isLocationByPlatform));
        set("isShowing", voidToBoolean(window::isShowing));
        set("getOpacity", voidToFloat(window::getOpacity));
        set("pack", voidToVoid(window::pack));
        set("setAlwaysOnTop", booleanOptToVoid(window::setAlwaysOnTop));
        set("setAutoRequestFocus", booleanToVoid(window::setAutoRequestFocus));
        set("setFocusableWindowState", booleanToVoid(window::setFocusableWindowState));
        set("setLocationByPlatform", booleanOptToVoid(window::setLocationByPlatform));
        set("setOpacity", floatToVoid(window::setOpacity));
        set("toBack", voidToVoid(window::toBack));
        set("toFront", voidToVoid(window::toFront));
    }
    
    
    private Value addWindowListener(Value[] args) {
        Arguments.check(1, args.length);
        final Function action = ValueUtils.consumeFunction(args[0], 0);
        window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                handleWindowEvent("opened", e);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowEvent("closing", e);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                handleWindowEvent("closed", e);
            }

            @Override
            public void windowIconified(WindowEvent e) {
                handleWindowEvent("iconified", e);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                handleWindowEvent("deiconified", e);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                handleWindowEvent("activated", e);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                handleWindowEvent("deactivated", e);
            }
            
            private void handleWindowEvent(String type, final WindowEvent e) {
                final MapValue map = new MapValue(4);
                map.set("id", NumberValue.of(e.getID()));
                map.set("newState", NumberValue.of(e.getNewState()));
                map.set("oldState", NumberValue.of(e.getOldState()));
                map.set("paramString", new StringValue(e.paramString()));
                action.execute(new StringValue(type), map);
            }
        });
        return NumberValue.ZERO;
    }
}