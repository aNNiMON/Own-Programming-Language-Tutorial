package com.annimon.ownlang.stages;

import java.util.HashMap;
import java.util.Map;

public class StagesDataMap implements StagesData {
    private final Map<String, Object> data = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String tag) {
        return (T) data.get(tag);
    }

    @Override
    public void put(String tag, Object input) {
        data.put(tag, input);
    }
}
