package com.annimon.ownlang.utils.repl;

public interface ReplConsole {

    void setPrompt(String prompt);

    String readLine();

    void close();
}
