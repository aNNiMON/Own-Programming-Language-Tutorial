package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import java.util.Scanner;

public final class std_readln implements Function {

    @Override
    public Value execute(Value[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            return new StringValue(sc.nextLine());
        }
    }
}
