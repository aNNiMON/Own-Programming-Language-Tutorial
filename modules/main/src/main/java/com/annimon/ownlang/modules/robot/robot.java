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
import static java.util.Map.entry;

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

    @Override
    public Map<String, Value> constants() {
        return Map.ofEntries(
                entry("VK_DOWN", NumberValue.of(KeyEvent.VK_DOWN)),
                entry("VK_LEFT", NumberValue.of(KeyEvent.VK_LEFT)),
                entry("VK_RIGHT", NumberValue.of(KeyEvent.VK_RIGHT)),
                entry("VK_FIRE", NumberValue.of(KeyEvent.VK_ENTER)),
                entry("VK_ESCAPE", NumberValue.of(KeyEvent.VK_ESCAPE)),

                entry("BUTTON1", NumberValue.of(InputEvent.BUTTON1_MASK)),
                entry("BUTTON2", NumberValue.of(InputEvent.BUTTON2_MASK)),
                entry("BUTTON3", NumberValue.of(InputEvent.BUTTON3_MASK))
        );
    }

    @Override
    public Map<String, Function> functions() {
        final var result = new HashMap<String, Function>(16);
        boolean isRobotInitialized = initialize();
        if (isRobotInitialized) {
            result.put("click", convertFunction(robot::click));
            result.put("delay", convertFunction(awtRobot::delay));
            result.put("setAutoDelay", convertFunction(awtRobot::setAutoDelay));
            result.put("keyPress", convertFunction(awtRobot::keyPress));
            result.put("keyRelease", convertFunction(awtRobot::keyRelease));
            result.put("mousePress", convertFunction(awtRobot::mousePress));
            result.put("mouseRelease", convertFunction(awtRobot::mouseRelease));
            result.put("mouseWheel", convertFunction(awtRobot::mouseWheel));
            result.put("mouseMove", this::mouseMove);
            result.put("typeText",this::typeText);
            result.put("toClipboard", new robot_toclipboard());
            result.put("fromClipboard", new robot_fromclipboard());
        }
        result.put("execProcess", new robot_exec(robot_exec.Mode.EXEC));
        result.put("execProcessAndWait", new robot_exec(robot_exec.Mode.EXEC_AND_WAIT));
        return result;
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

    private Value mouseMove(Value[] args) {
        Arguments.check(2, args.length);
        try {
            awtRobot.mouseMove(args[0].asInt(), args[1].asInt());
        } catch (IllegalArgumentException iae) { }
        return NumberValue.ZERO;
    }

    private Value typeText(Value[] args) {
        Arguments.check(1, args.length);
        try {
            typeText(args[0].asString());
        } catch (IllegalArgumentException iae) { }
        return NumberValue.ZERO;
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
