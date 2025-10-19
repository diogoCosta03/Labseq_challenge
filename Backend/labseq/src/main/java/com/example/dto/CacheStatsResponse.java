package com.example.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Response DTO for cache statistics.
 *
 * Provides information about the current state of the cache,
 * useful for monitoring and debugging.
 */
@Schema(description = "Cache statistics")
public class CacheStatsResponse {

    @Schema(description = "Number of values currently cached", example = "50")
    private int cacheSize;

    /**
     * Default constructor required for JSON-B deserialization.
     */
    public CacheStatsResponse() {
    }

    /**
     * Constructor with cache size.
     *
     * @param cacheSize number of values currently in cache
     */
    public CacheStatsResponse(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    // Getters and Setters

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String toString() {
        return "CacheStatsResponse{" +
                "cacheSize=" + cacheSize +
                '}';
    }
}