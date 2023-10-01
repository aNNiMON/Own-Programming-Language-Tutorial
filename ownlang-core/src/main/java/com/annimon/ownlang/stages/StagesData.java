package com.annimon.ownlang.stages;

public interface StagesData {

    <T> T get(String tag);

    default <T> T getOrDefault(String tag, T other) {
        T value = get(tag);
        return value != null ? value : other;
    }

    void put(String tag, Object input);
}
