package com.annimon.ownlang.utils;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.Main;
import com.annimon.ownlang.exceptions.LexerException;
import com.annimon.ownlang.exceptions.StoppedException;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.UserDefinedFunction;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.BlockStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.PrintVisitor;
import com.annimon.ownlang.utils.repl.JLineConsole;
import com.annimon.ownlang.utils.repl.ReplConsole;
import com.annimon.ownlang.utils.repl.SystemConsole;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class Repl {

    private static final String
            HELP = ":help",
            VARS = ":vars",
            FUNCS = ":funcs",
            SOURCE = ":source",
            RESET = ":reset",
            EXIT = ":exit";

    public static void main(String[] args) {
        System.out.println("Welcome to OwnLang " + Main.VERSION + " REPL");
        printHelp(false);

        final BlockStatement history = new BlockStatement();
        final StringBuilder buffer = new StringBuilder();
        final ReplConsole console = initReplConsole();
        while (true) {
            console.setPrompt((buffer.length() == 0) ? "\n> " : "  ");

            final String line = console.readLine();
            if (line == null || EXIT.equalsIgnoreCase(line)) break;

            switch (line.toLowerCase(Locale.ENGLISH)) {
                case RESET:
                    buffer.setLength(0);
                    continue;
                case HELP:
                    printHelp(true);
                    continue;
                case VARS:
                    printVariables();
                    continue;
                case FUNCS:
                    printFunctions();
                    continue;
                case SOURCE:
                    System.out.println(history.accept(new PrintVisitor(), new StringBuilder()));
                    continue;
            }

            buffer.append(line).append(Console.newline());
            Statement program = null;
            try {
                final List<Token> tokens = Lexer.tokenize(buffer.toString());
                final Parser parser = new Parser(tokens);
                program = parser.parse();
                if (parser.getParseErrors().hasErrors()) {
                    continue;
                }
                program.execute();
            } catch (LexerException lex) {
                continue;
            } catch (StoppedException ex) {
                // skip
            } catch (Exception ex) {
                Console.handleException(Thread.currentThread(), ex);
            }
            if (program != null) {
                history.add(program);
            }
            buffer.setLength(0);
        }
        console.close();
    }

    private static ReplConsole initReplConsole() {
        try {
            return new JLineConsole();
        } catch (IOException ioe) {
            return new SystemConsole();
        }
    }

    private static void printHelp(boolean full) {
        System.out.println("Type in expressions to have them evaluated.");
        final List<String> commands = new ArrayList<>();
        if (full) {
            commands.add(VARS + " - listing variables");
            commands.add(FUNCS + " - listing functions");
            commands.add(SOURCE + " - listing source");
        }
        commands.add(HELP + " - show help");
        commands.add(RESET + " - clear buffer");
        commands.add(EXIT + " - exit REPL");

        int maxLength = commands.stream()
                .mapToInt(String::length)
                .max().getAsInt();

        final int maxCols = 2;
        final int size = commands.size();
        for (int i = 0; i < size; i += maxCols) {
            // Pad to max length and print in tab-separatex maxCols columns
            System.out.println(commands
                    .subList(i, Math.min(size, i + maxCols))
                    .stream()
                    .map(str -> String.format("%-" + maxLength + "s", str))
                    .collect(Collectors.joining("\t", "  ", ""))
            );
        }
    }

    private static void printVariables() {
        Variables.variables().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("\t%s = %s%n",
                        e.getKey(), e.getValue().toString()));
    }

    private static void printFunctions() {
        System.out.println("User functions:");
        Functions.getFunctions().entrySet().stream()
                .filter(p -> p.getValue() instanceof UserDefinedFunction)
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("\t%s%s%n",
                        e.getKey(), ((UserDefinedFunction)e.getValue()).arguments));

        System.out.println("Library functions:");
        Functions.getFunctions().entrySet().stream()
                .filter(p -> !(p.getValue() instanceof UserDefinedFunction))
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("\t%s%n", e.getKey()));
    }
}
