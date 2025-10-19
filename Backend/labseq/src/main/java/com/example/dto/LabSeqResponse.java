package com.example.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Response DTO for labseq calculation results.
 *
 * Contains the calculated value along with performance metrics
 * and cache information.
 */
@Schema(description = "Response containing the calculated labseq value")
public class LabSeqResponse {

    @Schema(description = "The index in the sequence", example = "10")
    private int index;

    @Schema(description = "The calculated value at the given index", example = "3")
    private String value;

    @Schema(description = "Time taken to calculate in milliseconds", example = "5")
    private long calculationTimeMs;

    @Schema(description = "Current size of the cache", example = "11")
    private int cacheSize;

    /**
     * Default constructor required for JSON-B deserialization.
     */
    public LabSeqResponse() {
    }

    /**
     * Constructor with all fields.
     *
     * @param index the index in the sequence
     * @param value the calculated value (as string to support large numbers)
     * @param calculationTimeMs time taken to calculate in milliseconds
     * @param cacheSize current number of cached values
     */
    public LabSeqResponse(int index, String value, long calculationTimeMs, int cacheSize) {
        this.index = index;
        this.value = value;
        this.calculationTimeMs = calculationTimeMs;
        this.cacheSize = cacheSize;
    }

    // Getters and Setters

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getCalculationTimeMs() {
        return calculationTimeMs;
    }

    public void setCalculationTimeMs(long calculationTimeMs) {
        this.calculationTimeMs = calculationTimeMs;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String toString() {
        return "LabSeqResponse{" +
                "index=" + index +
                ", value='" + value + '\'' +
                ", calculationTimeMs=" + calculationTimeMs +
                ", cacheSize=" + cacheSize +
                '}';
    }
}