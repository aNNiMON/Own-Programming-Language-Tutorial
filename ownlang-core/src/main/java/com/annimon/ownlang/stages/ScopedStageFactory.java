package com.annimon.ownlang.stages;

import java.util.function.Consumer;

public final class ScopedStageFactory {
    private final Consumer<String> startStage;
    private final Consumer<String> endStage;

    public ScopedStageFactory(Consumer<String> startStage, Consumer<String> endStage) {
        this.startStage = startStage;
        this.endStage = endStage;
    }

    public <I, R> ScopedStage<I, R> create(String stageName, Stage<? super I, ? extends R> stage) {
        return new ScopedStage<>(stageName, stage, startStage, endStage);
    }
}
