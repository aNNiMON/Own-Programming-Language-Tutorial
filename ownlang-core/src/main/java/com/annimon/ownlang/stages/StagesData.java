package com.annimon.ownlang.stages;

import java.util.function.Supplier;

public interface StagesData {

    <T> T get(String tag);

    default <T> T getOrDefault(String tag, T other) {
        T value = get(tag);
        return value != null ? value : other;
    }

    default <T> T getOrDefault(String tag, Supplier<? extends T> otherSuppler) {
        T value = get(tag);
        return value != null ? value : otherSuppler.get();
    }

    void put(String tag, Object input);
}
