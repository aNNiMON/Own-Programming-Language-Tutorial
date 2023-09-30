package com.annimon.ownlang.stages;

import java.util.function.Consumer;

public class ScopedStage<I, R> implements Stage<I, R> {

    private final String stageName;
    private final Stage<? super I, ? extends R> stage;
    private final Consumer<String> startStage;
    private final Consumer<String> endStage;

    ScopedStage(String stageName, Stage<? super I, ? extends R> stage,
                Consumer<String> startStage, Consumer<String> endStage) {
        this.stageName = stageName;
        this.stage = stage;
        this.startStage = startStage;
        this.endStage = endStage;
    }

    @Override
    public R perform(StagesData stagesData, I input) {
        try {
            startStage.accept(stageName);
            return stage.perform(stagesData, input);
        } finally {
            endStage.accept(stageName);
        }
    }
}
