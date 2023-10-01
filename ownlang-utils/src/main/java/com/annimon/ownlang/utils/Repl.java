package com.annimon.ownlang.utils;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.Version;
import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.exceptions.StoppedException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.parser.*;
import com.annimon.ownlang.parser.ast.BlockStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.PrintVisitor;
import com.annimon.ownlang.utils.repl.JLineConsole;
import com.annimon.ownlang.utils.repl.OwnLangCompleter;
import com.annimon.ownlang.utils.repl.ReplConsole;
import com.annimon.ownlang.utils.repl.SystemConsole;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import jline.console.completer.CandidateListCompletionHandler;

public final class Repl {

    private static final String
            HELP = ":help",
            VARS = ":vars",
            FUNCS = ":funcs",
            SOURCE = ":source",
            RESET = ":reset",
            EXIT = ":exit";

    private static final Token PRINTLN_TOKEN = new Token(TokenType.PRINTLN, "", Pos.ZERO);

    public static void main(String[] args) {
        System.out.println("Welcome to OwnLang " + Version.VERSION + " REPL");
        printHelp(false);

        final BlockStatement history = new BlockStatement();
        final StringBuilder buffer = new StringBuilder();
        final ReplConsole console = initReplConsole();
        while (true) {
            console.setPrompt((buffer.isEmpty()) ? "\n> " : "  ");

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
                    // Try to print value
                    List<Token> tokens2 = new ArrayList<>();
                    tokens2.add(PRINTLN_TOKEN);
                    tokens2.addAll(tokens);
                    Parser parser2 = new Parser(tokens2);
                    program = parser2.parse();
                    if (parser2.getParseErrors().hasErrors()) {
                        continue;
                    }
                }
                program.execute();
            } catch (OwnLangParserException lex) {
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
            JLineConsole jline = new JLineConsole();
            CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
            handler.setPrintSpaceAfterFullCompletion(false);
            jline.getConsole().setCompletionHandler(handler);
            jline.getConsole().addCompleter(new OwnLangCompleter(HELP, VARS, FUNCS, SOURCE, RESET, EXIT));
            return jline;
        } catch (IOException ioe) {
            return new SystemConsole();
        }
    }

    private static void printHelp(boolean full) {
        System.out.println("Type in expressions to have them evaluated.");
        final List<String> commands = new ArrayList<>();
        if (full) {
            commands.add(VARS + " - list variables/constants");
            commands.add(FUNCS + " - list functions");
            commands.add(SOURCE + " - show source");
        }
        commands.add(HELP + " - show help");
        commands.add(RESET + " - clear buffer");
        commands.add(EXIT + " - exit REPL");

        int maxLength = commands.stream()
                .mapToInt(String::length)
                .max()
                .orElse(20);

        final int maxCols = 2;
        final int size = commands.size();
        for (int i = 0; i < size; i += maxCols) {
            // Pad to max length and print in tab-separated maxCols columns
            System.out.println(commands
                    .subList(i, Math.min(size, i + maxCols))
                    .stream()
                    .map(str -> String.format("%-" + maxLength + "s", str))
                    .collect(Collectors.joining("\t", "  ", ""))
            );
        }
    }

    private static void printVariables() {
        System.out.println("Variables:");
        ScopeHandler.variables().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %s = %s%n",
                        e.getKey(), e.getValue().toString()));

        System.out.println("Constants:");
        ScopeHandler.constants().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %s = %s%n",
                        e.getKey(), e.getValue().toString()));
    }

    private static void printFunctions() {
        if (ScopeHandler.functions().isEmpty()) {
            System.out.println("No functions declared yet!");
            return;
        }

        final var functions = ScopeHandler.functions().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.partitioningBy(p -> p.getValue() instanceof UserDefinedFunction));

        final var userFunctions = functions.get(true);
        if (!userFunctions.isEmpty()) {
            System.out.println("User functions:");
            for (Map.Entry<String, Function> e : userFunctions) {
                System.out.printf("  %s%s%n",
                        e.getKey(), ((UserDefinedFunction) e.getValue()).arguments);
            }
        }

        final var libraryFunctions = functions.get(false);
        if (!libraryFunctions.isEmpty()) {
            System.out.printf("Library functions:%n  ");
            final var libraryFunctionNames = libraryFunctions.stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(", "));
            System.out.println(libraryFunctionNames);
        }
    }
}
