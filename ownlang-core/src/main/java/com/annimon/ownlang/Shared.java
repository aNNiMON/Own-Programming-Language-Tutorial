package com.annimon.ownlang;

public final class Shared {
    private static String[] ownlangArgs = new String[0];

    public static String[] getOwnlangArgs() {
        return ownlangArgs;
    }

    public static void setOwnlangArgs(String[] args) {
        ownlangArgs = args;
    }
}
