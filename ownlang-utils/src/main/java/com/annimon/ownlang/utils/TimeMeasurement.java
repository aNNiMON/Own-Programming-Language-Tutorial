package com.annimon.ownlang.utils;

import com.annimon.ownlang.Console;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class TimeMeasurement {

    private final Map<String, Long> finished, running;
    
    public TimeMeasurement() {
        finished = new LinkedHashMap<>();
        running = new LinkedHashMap<>();
    }
    
    public void clear() {
        finished.clear();
        running.clear();
    }
    
    public void start(String name) {
        running.put(name, System.nanoTime());
    }
    
    public void pause(String name) {
        if (running.containsKey(name)) {
            addTime(name, System.nanoTime() - running.get(name));
            running.remove(name);
        }
    }
    
    public void stop(String name) {
        if (running.containsKey(name)) {
            addTime(name, System.nanoTime() - running.get(name));
        }
    }
    
    public Map<String, Long> getFinished() {
        return finished;
    }
    
    public String summary() {
        return summary(TimeUnit.SECONDS, true);
    }
    
    public String summary(TimeUnit unit, boolean showSummary) {
        final String unitName = unit.name().toLowerCase();
        final StringBuilder result = new StringBuilder();
        long summaryTime = 0;
        for (Map.Entry<String, Long> entry : finished.entrySet()) {
            final long convertedTime = unit.convert(entry.getValue(), TimeUnit.NANOSECONDS);
            summaryTime += convertedTime;
            
            result.append(entry.getKey()).append(": ")
                    .append(convertedTime).append(' ').append(unitName)
                    .append(Console.newline());
        }
        if (showSummary) {
            result.append("Summary: ")
                        .append(summaryTime).append(' ').append(unitName)
                        .append(Console.newline());
        }
        return result.toString();
    }
    
    private void addTime(String name, long time) {
        final long alreadyElapsed = finished.getOrDefault(name, 0L);
        finished.put(name, alreadyElapsed + time);
    }
}
