package com.annimon.ownlang.modules.robot;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

/**
 *
 * @author aNNiMON
 */
public final class robot implements Module {
    
    private static final int CLICK_DELAY = 200;
    private static final int TYPING_DELAY = 50;
    
    private static final Map<Character, Integer> SYMBOL_CODES;
    static {
        SYMBOL_CODES = new HashMap<>(10);
        SYMBOL_CODES.put('_', KeyEvent.VK_MINUS);
        SYMBOL_CODES.put(':', KeyEvent.VK_SEMICOLON);
    }
    
    private static Robot awtRobot;

    public static void initConstants() {
        ScopeHandler.setConstant("VK_DOWN", NumberValue.of(KeyEvent.VK_DOWN));
        ScopeHandler.setConstant("VK_LEFT", NumberValue.of(KeyEvent.VK_LEFT));
        ScopeHandler.setConstant("VK_RIGHT", NumberValue.of(KeyEvent.VK_RIGHT));
        ScopeHandler.setConstant("VK_FIRE", NumberValue.of(KeyEvent.VK_ENTER));
        ScopeHandler.setConstant("VK_ESCAPE", NumberValue.of(KeyEvent.VK_ESCAPE));

        ScopeHandler.setConstant("BUTTON1", NumberValue.of(InputEvent.BUTTON1_MASK));
        ScopeHandler.setConstant("BUTTON2", NumberValue.of(InputEvent.BUTTON2_MASK));
        ScopeHandler.setConstant("BUTTON3", NumberValue.of(InputEvent.BUTTON3_MASK));
    }

    @Override
    public void init() {
        initConstants();
        boolean isRobotInitialized = initialize();
        if (isRobotInitialized) {
            ScopeHandler.setFunction("click", convertFunction(robot::click));
            ScopeHandler.setFunction("delay", convertFunction(awtRobot::delay));
            ScopeHandler.setFunction("setAutoDelay", convertFunction(awtRobot::setAutoDelay));
            ScopeHandler.setFunction("keyPress", convertFunction(awtRobot::keyPress));
            ScopeHandler.setFunction("keyRelease", convertFunction(awtRobot::keyRelease));
            ScopeHandler.setFunction("mousePress", convertFunction(awtRobot::mousePress));
            ScopeHandler.setFunction("mouseRelease", convertFunction(awtRobot::mouseRelease));
            ScopeHandler.setFunction("mouseWheel", convertFunction(awtRobot::mouseWheel));
            ScopeHandler.setFunction("mouseMove", (args) -> {
                Arguments.check(2, args.length);
                try {
                    awtRobot.mouseMove(args[0].asInt(), args[1].asInt());
                } catch (IllegalArgumentException iae) { }
                return NumberValue.ZERO;
            });
            ScopeHandler.setFunction("typeText", (args) -> {
                Arguments.check(1, args.length);
                try {
                    typeText(args[0].asString());
                } catch (IllegalArgumentException iae) { }
                return NumberValue.ZERO;
            });
            ScopeHandler.setFunction("toClipboard", new robot_toclipboard());
            ScopeHandler.setFunction("fromClipboard", new robot_fromclipboard());
        }
        ScopeHandler.setFunction("execProcess", new robot_exec(robot_exec.Mode.EXEC));
        ScopeHandler.setFunction("execProcessAndWait", new robot_exec(robot_exec.Mode.EXEC_AND_WAIT));
    }
    
    private static boolean initialize() {
        try {
            awtRobot = new Robot();
            return true;
        } catch (AWTException awte) {
            //throw new RuntimeException("Unable to create robot instance", awte);
            return false;
        }
    }
    
    private static Function convertFunction(IntConsumer consumer) {
        return args -> {
            Arguments.check(1, args.length);
            try {
                consumer.accept(args[0].asInt());
            } catch (IllegalArgumentException iae) { }
            return NumberValue.ZERO;
        };
    }
    
    private static synchronized void click(int buttons) {
        awtRobot.mousePress(buttons);
        awtRobot.delay(CLICK_DELAY);
        awtRobot.mouseRelease(buttons);
    }
 
    private static synchronized void typeText(String text) {
        for (char ch : text.toCharArray()) {
            typeSymbol(ch);
        }
    }

    private static void typeSymbol(char ch) {
        int code = KeyEvent.getExtendedKeyCodeForChar(ch);

        boolean isUpperCase = Character.isLetter(ch) && Character.isUpperCase(ch);
        boolean needPressShift = isUpperCase;
        if (!isUpperCase) {
            final int symbolIndex = "!@#$%^&*()".indexOf(ch);
            if (symbolIndex != -1) {
                needPressShift = true;
                code = '1' + symbolIndex;
            } else if (SYMBOL_CODES.containsKey(ch)) {
                needPressShift = true;
                code = SYMBOL_CODES.get(ch);
            }
        }

        if (code == KeyEvent.VK_UNDEFINED) return;

        if (needPressShift) {
            // press shift
            awtRobot.keyPress(KeyEvent.VK_SHIFT);
            awtRobot.delay(TYPING_DELAY);
        }

        awtRobot.keyPress(code);
        awtRobot.delay(TYPING_DELAY);
        awtRobot.keyRelease(code);

        if (needPressShift) {
            // release shift
            awtRobot.delay(TYPING_DELAY);
            awtRobot.keyRelease(KeyEvent.VK_SHIFT);
            awtRobot.delay(TYPING_DELAY);
        }
    }
}
