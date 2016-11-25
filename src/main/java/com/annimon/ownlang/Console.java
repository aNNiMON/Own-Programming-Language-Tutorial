package com.annimon.ownlang;

import com.annimon.ownlang.lib.CallStack;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Console {

    private static final String FILE_PREFIX = "tmp/";
    public static boolean filePrefixEnabled = false;

    public static String newline() {
        return System.lineSeparator();
    }

    public static void print(String value) {
        System.out.print(value);
    }

    public static void print(Object value) {
        print(value.toString());
    }

    public static void println() {
        System.out.println();
    }

    public static void println(String value) {
        System.out.println(value);
    }

    public static void println(Object value) {
        println(value.toString());
    }

    public static void error(Throwable throwable) {
        error(throwable.toString());
    }

    public static void error(CharSequence value) {
        System.err.println(value);
    }

    public static void handleException(Thread thread, Throwable throwable) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(final PrintStream ps = new PrintStream(baos)) {
            ps.printf("%s in %s\n", throwable.getMessage(), thread.getName());
            for (CallStack.CallInfo call : CallStack.getCalls()) {
                ps.printf("\tat %s\n", call);
            }
            ps.println();
            throwable.printStackTrace(ps);
            ps.flush();
        }
        try {
            error(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            error(baos.toString());
        }
    }

    public static File fileInstance(String path) {
        final String filepath;
        if (filePrefixEnabled) {
            filepath = FILE_PREFIX.concat(path);
        } else {
            filepath = path;
        }
        return new File(filepath);
    }
}
