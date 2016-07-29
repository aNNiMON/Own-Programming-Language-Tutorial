package com.annimon.ownlang.lib.modules.functions.forms;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import static com.annimon.ownlang.lib.Converters.*;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ComponentValue extends MapValue {

    final Component component;

    public ComponentValue(int functionsCount, Component component) {
        super(functionsCount + 42);
        this.component = component;
        init();
    }

    private void init() {
        set(new StringValue("onKeyAction"), new FunctionValue(this::addKeyListener));
        set(new StringValue("addKeyListener"), new FunctionValue(this::addKeyListener));
        set(new StringValue("getFocusTraversalKeysEnabled"), voidToBoolean(component::getFocusTraversalKeysEnabled));
        set(new StringValue("getHeight"), voidToInt(component::getHeight));
        set(new StringValue("getIgnoreRepaint"), voidToBoolean(component::getIgnoreRepaint));
        set(new StringValue("getLocation"), new FunctionValue(this::getLocation));
        set(new StringValue("getLocationOnScreen"), new FunctionValue(this::getLocationOnScreen));
        set(new StringValue("getMinimumSize"), dimensionFunction(component::getMinimumSize));
        set(new StringValue("getMaximumSize"), dimensionFunction(component::getMaximumSize));
        set(new StringValue("getName"), voidToString(component::getName));
        set(new StringValue("getPreferredSize"), dimensionFunction(component::getPreferredSize));
        set(new StringValue("getSize"), dimensionFunction(component::getSize));
        set(new StringValue("getWidth"), voidToInt(component::getWidth));
        set(new StringValue("getX"), voidToInt(component::getX));
        set(new StringValue("getY"), voidToInt(component::getY));
        set(new StringValue("hasFocus"), voidToBoolean(component::hasFocus));
        set(new StringValue("invalidate"), voidToVoid(component::invalidate));

        set(new StringValue("isDisplayable"), voidToBoolean(component::isDisplayable));
        set(new StringValue("isDoubleBuffered"), voidToBoolean(component::isDoubleBuffered));
        set(new StringValue("isEnabled"), voidToBoolean(component::isEnabled));
        set(new StringValue("isFocusOwner"), voidToBoolean(component::isFocusOwner));
        set(new StringValue("isFocusable"), voidToBoolean(component::isFocusable));
        set(new StringValue("isLightweight"), voidToBoolean(component::isLightweight));
        set(new StringValue("isOpaque"), voidToBoolean(component::isOpaque));
        set(new StringValue("isShowing"), voidToBoolean(component::isShowing));
        set(new StringValue("isValid"), voidToBoolean(component::isValid));
        set(new StringValue("isVisible"), voidToBoolean(component::isVisible));

        set(new StringValue("requestFocus"), voidToVoid(component::requestFocus));
        set(new StringValue("requestFocusInWindow"), voidToBoolean(component::requestFocusInWindow));
        set(new StringValue("repaint"), voidToVoid(component::repaint));
        set(new StringValue("revalidate"), voidToVoid(component::revalidate));
        set(new StringValue("setMaximumSize"), voidDimensionFunction(component::setMaximumSize));
        set(new StringValue("setMinimumSize"), voidDimensionFunction(component::setMinimumSize));
        set(new StringValue("setName"), stringToVoid(component::setName));
        set(new StringValue("setPreferredSize"), voidDimensionFunction(component::setPreferredSize));
        set(new StringValue("setSize"), voidDimensionFunction(component::setSize));
        set(new StringValue("setVisible"), booleanOptToVoid(component::setVisible));
        set(new StringValue("setLocation"), new FunctionValue(this::setLocation));
        set(new StringValue("validate"), voidToVoid(component::validate));
    }

    private Value addKeyListener(Value... args) {
        Arguments.check(1, args.length);
        final int type = args[0].type();
        if (type != Types.FUNCTION) {
            throw new TypeException("Function expected, but found " + Types.typeToString(type));
        }
        final Function action = ((FunctionValue) args[0]).getValue();
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
                map.set(new StringValue("extendedKeyCode"), NumberValue.of(e.getExtendedKeyCode()));
                map.set(new StringValue("keyChar"), NumberValue.of(e.getKeyChar()));
                map.set(new StringValue("keyCode"), NumberValue.of(e.getKeyCode()));
                map.set(new StringValue("keyLocation"), NumberValue.of(e.getKeyLocation()));
                map.set(new StringValue("id"), NumberValue.of(e.getID()));
                map.set(new StringValue("isActionKey"), NumberValue.fromBoolean(e.isActionKey()));
                map.set(new StringValue("isAltDown"), NumberValue.fromBoolean(e.isAltDown()));
                map.set(new StringValue("isAltGraphDown"), NumberValue.fromBoolean(e.isAltGraphDown()));
                map.set(new StringValue("isConsumed"), NumberValue.fromBoolean(e.isConsumed()));
                map.set(new StringValue("isControlDown"), NumberValue.fromBoolean(e.isControlDown()));
                map.set(new StringValue("isMetaDown"), NumberValue.fromBoolean(e.isMetaDown()));
                map.set(new StringValue("isShiftDown"), NumberValue.fromBoolean(e.isShiftDown()));
                map.set(new StringValue("modifiers"), NumberValue.of(e.getModifiers()));
                action.execute(new StringValue(type), map);
            }
        });
        return NumberValue.ZERO;
    }

    private Value getLocation(Value... args) {
        final Point location = component.getLocation();
        final ArrayValue result = new ArrayValue(2);
        result.set(0, NumberValue.of(location.x));
        result.set(1, NumberValue.of(location.y));
        return result;
    }

    private Value getLocationOnScreen(Value... args) {
        final Point location = component.getLocationOnScreen();
        final ArrayValue result = new ArrayValue(2);
        result.set(0, NumberValue.of(location.x));
        result.set(1, NumberValue.of(location.y));
        return result;
    }

    private Value setLocation(Value... args) {
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