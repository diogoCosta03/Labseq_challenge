package com.example.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Response DTO for simple messages.
 *
 * Used for operations that return a confirmation message
 * along with additional information.
 */
@Schema(description = "Simple message response")
public class MessageResponse {

    @Schema(description = "Message content", example = "Cache cleared successfully")
    private String message;

    @Schema(description = "Current cache size after operation", example = "4")
    private int cacheSize;

    /**
     * Default constructor required for JSON-B deserialization.
     */
    public MessageResponse() {
    }

    /**
     * Constructor with message and cache size.
     *
     * @param message the message content
     * @param cacheSize current cache size after the operation
     */
    public MessageResponse(String message, int cacheSize) {
        this.message = message;
        this.cacheSize = cacheSize;
    }

    // Getters and Setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "message='" + message + '\'' +
                ", cacheSize=" + cacheSize +
                '}';
    }
}