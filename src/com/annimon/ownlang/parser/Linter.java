package com.annimon.ownlang.parser;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.parser.linters.AssignValidator;
import com.annimon.ownlang.parser.linters.UseWithNonStringValueValidator;
import com.annimon.ownlang.parser.linters.DefaultFunctionsOverrideValidator;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.Visitor;

public final class Linter {

    public static void lint(Statement program) {
        new Linter(program).execute();
    }

    private final Statement program;

    private Linter(Statement program) {
        this.program = program;
    }
    
    public void execute() {
        final Visitor[] validators = new Visitor[] {
            new UseWithNonStringValueValidator(),
            new AssignValidator(),
            new DefaultFunctionsOverrideValidator()
        };
        resetState();
        for (Visitor validator : validators) {
            program.accept(validator);
            resetState();
        }
        Console.println("Lint validation complete!");
    }

    private void resetState() {
        Variables.clear();
        Functions.getFunctions().clear();
    }
}
