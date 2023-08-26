package com.annimon.ownlang.utils.repl;

import java.util.Scanner;

public class SystemConsole implements ReplConsole {

    private final Scanner scanner;

    public SystemConsole() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void setPrompt(String prompt) {
        System.out.print(prompt);
    }

    @Override
    public String readLine() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    @Override
    public void close() {
        scanner.close();
    }

}
