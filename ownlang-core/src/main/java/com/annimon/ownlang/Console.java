package com.annimon.ownlang;

import com.annimon.ownlang.lib.CallStack;
import com.annimon.ownlang.outputsettings.ConsoleOutputSettings;
import com.annimon.ownlang.outputsettings.OutputSettings;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Console {

    private Console() { }
    
    private static OutputSettings outputSettings = new ConsoleOutputSettings();

    public static void useSettings(OutputSettings outputSettings) {
        Console.outputSettings = outputSettings;
    }

    public static OutputSettings getSettings() {
        return outputSettings;
    }

    public static String newline() {
        return outputSettings.newline();
    }

    public static void print(String value) {
        outputSettings.print(value);
    }

    public static void print(Object value) {
        outputSettings.print(value);
    }

    public static void println() {
        outputSettings.println();
    }

    public static void println(String value) {
        outputSettings.println(value);
    }

    public static void println(Object value) {
        outputSettings.println(value);
    }

    public static String text() {
        return outputSettings.getText();
    }

    public static void error(Throwable throwable) {
        outputSettings.error(throwable);
    }

    public static void error(CharSequence value) {
        outputSettings.error(value);
    }

    public static void handleException(Thread thread, Throwable throwable) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(final PrintStream ps = new PrintStream(baos)) {
            ps.printf("%s in %s%n", throwable.getMessage(), thread.getName());
            for (CallStack.CallInfo call : CallStack.getCalls()) {
                ps.printf("\tat %s%n", call);
            }
            ps.println();
            throwable.printStackTrace(ps);
            ps.flush();
        }
        error(baos.toString(StandardCharsets.UTF_8));
    }

    public static File fileInstance(String path) {
        return outputSettings.fileInstance(path);
    }
}
