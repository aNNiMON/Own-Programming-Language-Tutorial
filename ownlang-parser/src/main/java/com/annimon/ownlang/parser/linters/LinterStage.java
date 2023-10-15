package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LinterStage implements Stage<Node, Node> {
    public enum Mode { NONE, SEMANTIC, FULL }

    private final Mode mode;

    public LinterStage(Mode mode) {
        this.mode = mode;
    }

    @Override
    public Node perform(StagesData stagesData, Node input) {
        if (mode == Mode.NONE) return input;

        final LinterResults results = new LinterResults();
        final List<Visitor> validators = new ArrayList<>();
        validators.add(new IncludeSourceValidator(results));

        if (mode == Mode.SEMANTIC) {
            validators.forEach(input::accept);
            if (results.hasErrors()) {
                throw new OwnLangParserException(results.errors().toList());
            }
            return input;
        }

        // Full lint validation with Console output
        validators.add(new AssignValidator(results));
        validators.add(new DefaultFunctionsOverrideValidator(results));

        ScopeHandler.resetScope(); // TODO special linter scope?
        for (Visitor validator : validators) {
            input.accept(validator);
            ScopeHandler.resetScope();
        }

        results.sort(Comparator.comparing(LinterResult::severity));
        Console.println("Lint validation completed. %d results found!".formatted(results.size()));
        for (LinterResult r : results) {
            switch (r.severity()) {
                case ERROR -> Console.error(r.toString());
                case WARNING -> Console.println(r.toString());
            }
        }
        return input;
    }
}
