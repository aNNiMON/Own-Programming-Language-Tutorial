package com.annimon.ownlang.stages;

public interface Stage<I, R> {
    R perform(StagesData stagesData, I input);

    default <NR> Stage<I, NR> then(Stage<? super R, ? extends NR> next) {
        return (scope, input) -> {
            R result = perform(scope, input);
            return next.perform(scope, result);
        };
    }

    default Stage<I, R> thenConditional(boolean enabled, Stage<? super R, ? extends R> next) {
        return enabled ? then(next) : this;
    }
}
