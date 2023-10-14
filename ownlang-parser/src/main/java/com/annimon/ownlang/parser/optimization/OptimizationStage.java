package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;

public class OptimizationStage implements Stage<Node, Node> {

    public static final String TAG_OPTIMIZATION_SUMMARY = "optimizationSummary";

    private final int level;
    private final boolean summary;
    private final Optimizable optimization;

    public OptimizationStage(int level) {
        this(level, false);
    }

    public OptimizationStage(int level, boolean summary) {
        this.level = level;
        this.summary = summary;
        optimization = new SummaryOptimization(new Optimizable[] {
                new ConstantFolding(),
                new ConstantPropagation(),
                new DeadCodeElimination(),
                new ExpressionSimplification(),
                new InstructionCombining()
        });
    }

    @Override
    public Node perform(StagesData stagesData, Node input) {
        if (level == 0) return input;

        Node result = input;
        final int maxIterations = level >= 9 ? Integer.MAX_VALUE : level;
        int lastModifications;
        int iteration = 0;
        do {
            lastModifications = optimization.optimizationsCount();
            result = optimization.optimize(result);
            iteration++;
        } while (lastModifications != optimization.optimizationsCount() && iteration < maxIterations);

        if (this.summary) {
            stagesData.put(TAG_OPTIMIZATION_SUMMARY, """
                Performed %d optimization iterations
                %s
                """.formatted(iteration, optimization.summaryInfo())
            );
        }

        return result;
    }
}
