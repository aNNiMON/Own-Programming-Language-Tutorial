package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static com.annimon.ownlang.lib.Converters.booleanOptToVoid;
import static com.annimon.ownlang.lib.Converters.stringToVoid;
import static com.annimon.ownlang.lib.Converters.voidToBoolean;
import static com.annimon.ownlang.lib.Converters.voidToInt;
import static com.annimon.ownlang.lib.Converters.voidToString;
import static com.annimon.ownlang.lib.Converters.voidToVoid;

public abstract class ComponentValue extends MapValue {

    final Component component;

    public ComponentValue(int functionsCount, Component component) {
        super(functionsCount + 42);
        this.component = component;
        init();
    }

    private void init() {
        set("onKeyAction", new FunctionValue(this::addKeyListener));
        set("addKeyListener", new FunctionValue(this::addKeyListener));
        set("getFocusTraversalKeysEnabled", voidToBoolean(component::getFocusTraversalKeysEnabled));
        set("getHeight", voidToInt(component::getHeight));
        set("getIgnoreRepaint", voidToBoolean(component::getIgnoreRepaint));
        set("getLocation", new FunctionValue(this::getLocation));
        set("getLocationOnScreen", new FunctionValue(this::getLocationOnScreen));
        set("getMinimumSize", dimensionFunction(component::getMinimumSize));
        set("getMaximumSize", dimensionFunction(component::getMaximumSize));
        set("getName", voidToString(component::getName));
        set("getPreferredSize", dimensionFunction(component::getPreferredSize));
        set("getSize", dimensionFunction(component::getSize));
        set("getWidth", voidToInt(component::getWidth));
        set("getX", voidToInt(component::getX));
        set("getY", voidToInt(component::getY));
        set("hasFocus", voidToBoolean(component::hasFocus));
        set("invalidate", voidToVoid(component::invalidate));

        set("isDisplayable", voidToBoolean(component::isDisplayable));
        set("isDoubleBuffered", voidToBoolean(component::isDoubleBuffered));
        set("isEnabled", voidToBoolean(component::isEnabled));
        set("isFocusOwner", voidToBoolean(component::isFocusOwner));
        set("isFocusable", voidToBoolean(component::isFocusable));
        set("isLightweight", voidToBoolean(component::isLightweight));
        set("isOpaque", voidToBoolean(component::isOpaque));
        set("isShowing", voidToBoolean(component::isShowing));
        set("isValid", voidToBoolean(component::isValid));
        set("isVisible", voidToBoolean(component::isVisible));

        set("requestFocus", voidToVoid(component::requestFocus));
        set("requestFocusInWindow", voidToBoolean(component::requestFocusInWindow));
        set("repaint", voidToVoid(component::repaint));
        set("revalidate", voidToVoid(component::revalidate));
        set("setMaximumSize", voidDimensionFunction(component::setMaximumSize));
        set("setMinimumSize", voidDimensionFunction(component::setMinimumSize));
        set("setName", stringToVoid(component::setName));
        set("setPreferredSize", voidDimensionFunction(component::setPreferredSize));
        set("setSize", voidDimensionFunction(component::setSize));
        set("setVisible", booleanOptToVoid(component::setVisible));
        set("setLocation", new FunctionValue(this::setLocation));
        set("validate", voidToVoid(component::validate));
    }

    private Value addKeyListener(Value[] args) {
        Arguments.check(1, args.length);
        final Function action = ValueUtils.consumeFunction(args[0], 0);
        component.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                handleKeyEvent("typed", e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent("pressed", e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyEvent("released", e);
            }

            private void handleKeyEvent(String type, final KeyEvent e) {
                final MapValue map = new MapValue(15);
                map.set("extendedKeyCode", NumberValue.of(e.getExtendedKeyCode()));
                map.set("keyChar", NumberValue.of(e.getKeyChar()));
                map.set("keyCode", NumberValue.of(e.getKeyCode()));
                map.set("keyLocation", NumberValue.of(e.getKeyLocation()));
                map.set("id", NumberValue.of(e.getID()));
                map.set("isActionKey", NumberValue.fromBoolean(e.isActionKey()));
                map.set("isAltDown", NumberValue.fromBoolean(e.isAltDown()));
                map.set("isAltGraphDown", NumberValue.fromBoolean(e.isAltGraphDown()));
                map.set("isConsumed", NumberValue.fromBoolean(e.isConsumed()));
                map.set("isControlDown", NumberValue.fromBoolean(e.isControlDown()));
                map.set("isMetaDown", NumberValue.fromBoolean(e.isMetaDown()));
                map.set("isShiftDown", NumberValue.fromBoolean(e.isShiftDown()));
                map.set("modifiers", NumberValue.of(e.getModifiers()));
                action.execute(new StringValue(type), map);
            }
        });
        return NumberValue.ZERO;
    }

    private Value getLocation(Value[] args) {
        final Point location = component.getLocation();
        final ArrayValue result = new ArrayValue(2);
        result.set(0, NumberValue.of(location.x));
        result.set(1, NumberValue.of(location.y));
        return result;
    }

    private Value getLocationOnScreen(Value[] args) {
        final Point location = component.getLocationOnScreen();
        final ArrayValue result = new ArrayValue(2);
        result.set(0, NumberValue.of(location.x));
        result.set(1, NumberValue.of(location.y));
        return result;
    }

    private Value setLocation(Value[] args) {
        Arguments.check(2, args.length);
        component.setLocation(args[0].asInt(), args[1].asInt());
        return NumberValue.ZERO;
    }



    protected static FunctionValue dimensionFunction(Supplier<Dimension> s) {
        return new FunctionValue(args -> {
            final Dimension dimension = s.get();
            final ArrayValue result = new ArrayValue(2);
            result.set(0, NumberValue.of(dimension.getWidth()));
            result.set(1, NumberValue.of(dimension.getHeight()));
            return result;
        });
    }

    protected static FunctionValue voidDimensionFunction(Consumer<Dimension> s) {
        return new FunctionValue(args -> {
            Arguments.check(2, args.length);
            s.accept(new Dimension(args[0].asInt(), args[1].asInt()));
            return NumberValue.ZERO;
        });
    }
}