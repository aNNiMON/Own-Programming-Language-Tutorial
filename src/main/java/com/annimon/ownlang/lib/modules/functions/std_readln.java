package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.*;
import java.util.Scanner;

public final class std_readln implements Function {

    @Override
    public Value execute(Value... args) {
        try (Scanner sc = new Scanner(System.in)) {
            return new StringValue(sc.nextLine());
        }
    }
}