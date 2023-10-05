package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import java.io.UnsupportedEncodingException;

public final class StringFunctions {

    private StringFunctions() { }

    static ArrayValue getBytes(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final String charset = (args.length == 2) ? args[1].asString() : "UTF-8";
        try {
            return ArrayValue.of(args[0].asString().getBytes(charset));
        } catch (UnsupportedEncodingException uee) {
            throw new OwnLangRuntimeException(uee);
        }
    }
    
    static Value parseInt(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final int radix = (args.length == 2) ? args[1].asInt() : 10;
        return NumberValue.of(Integer.parseInt(args[0].asString(), radix));
    }

    static Value parseLong(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final int radix = (args.length == 2) ? args[1].asInt() : 10;
        return NumberValue.of(Long.parseLong(args[0].asString(), radix));
    }
    
    static Value stripMargin(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final String input = args[0].asString();
        final String marginPrefix = (args.length == 2) ? args[1].asString() : "|";
        if (!input.contains(marginPrefix)) {
            return args[0];
        }
        final String[] lines = input.split("\\r?\\n");

        // First blank line is omitted
        final StringBuilder sb = new StringBuilder();
        final int firstLineIndex = (isBlank(lines[0])) ? 1 : 0;
        final int lastLineIndex = lines.length - 1;
        int index = firstLineIndex;
        while (true) {
            sb.append(strip(lines[index], marginPrefix));
            if (++index >= lastLineIndex) break;
            sb.append('\n');
        }
        // Process last line
        if (lastLineIndex >= (firstLineIndex + 1) && !isBlank(lines[lastLineIndex])) {
            sb.append('\n').append(strip(lines[lastLineIndex], marginPrefix));
        }
        return new StringValue(sb.toString());
    }
    
    private static String strip(String str, String marginPrefix) {
        final int nonBlankIndex = firstNonBlankIndex(str);
        if (str.startsWith(marginPrefix, nonBlankIndex)) {
            return str.substring(nonBlankIndex + marginPrefix.length());
        } else {
            return str;
        }
    }

    private static boolean isBlank(String str) {
        return firstNonBlankIndex(str) == str.length();
    }
    
    private static int firstNonBlankIndex(String str) {
        final int length = str.length();
        for (int index = 0; index < length; index++) {
            final char ch = str.charAt(index);
            if (ch != ' ' && ch != '\t' && !Character.isWhitespace(ch)) {
                return index;
            }
        }
        return length;
    }
}
