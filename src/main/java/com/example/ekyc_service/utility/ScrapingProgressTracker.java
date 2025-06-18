package com.example.ekyc_service.utility;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ScrapingProgressTracker {

    private final ConcurrentMap<String, Integer> progressMap = new ConcurrentHashMap<>();

    public void setProgress(String key, int progress) {
        progressMap.put(key, progress);
    }

    public int getProgress(String key) {
        return progressMap.getOrDefault(key, 0);
    }

    public void clearProgress(String key) {
        progressMap.remove(key);
    }
}

