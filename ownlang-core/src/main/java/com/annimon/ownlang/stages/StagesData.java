package com.annimon.ownlang.stages;

public interface StagesData {

    <T> T get(String tag);

    void put(String tag, Object input);
}
