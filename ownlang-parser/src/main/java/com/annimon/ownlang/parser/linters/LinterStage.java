package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LinterStage implements Stage<Node, Node> {

    @Override
    public Node perform(StagesData stagesData, Node input) {
        final List<LinterResult> results = new ArrayList<>();
        final Visitor[] validators = new Visitor[] {
                new AssignValidator(results),
                new DefaultFunctionsOverrideValidator(results)
        };

        ScopeHandler.resetScope();
        for (Visitor validator : validators) {
            input.accept(validator);
            ScopeHandler.resetScope();
        }

        results.sort(Comparator.comparing(LinterResult::severity));
        Console.println(String.format("Lint validation completed. %d results found!", results.size()));
        for (LinterResult r : results) {
            switch (r.severity()) {
                case ERROR -> Console.error(r.toString());
                case WARNING -> Console.println(r.toString());
            }
        }
        return input;
    }
}
