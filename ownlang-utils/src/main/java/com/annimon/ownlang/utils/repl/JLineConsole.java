package com.annimon.ownlang.utils.repl;

import java.io.IOException;
import jline.TerminalFactory;
import jline.console.ConsoleReader;

public class JLineConsole implements ReplConsole {

    private final ConsoleReader console;

    public JLineConsole() throws IOException {
        System.setProperty(ConsoleReader.JLINE_EXPAND_EVENTS, "false");
        console = new ConsoleReader();
    }

    public ConsoleReader getConsole() {
        return console;
    }

    @Override
    public void setPrompt(String prompt) {
        console.setPrompt(prompt);
    }

    @Override
    public String readLine() {
        try {
            return console.readLine();
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public void close() {
        try {
            TerminalFactory.get().restore();
        } catch (Exception ignored) {
        }
    }

}
