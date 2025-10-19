package com.example.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Response DTO for error messages.
 *
 * Used to provide structured error information to clients
 * when requests fail due to validation or processing errors.
 */
@Schema(description = "Error response")
public class ErrorResponse {

    @Schema(description = "Error type", example = "Invalid input")
    private String error;

    @Schema(description = "Detailed error message", example = "Index must be non-negative")
    private String message;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    /**
     * Default constructor required for JSON-B deserialization.
     */
    public ErrorResponse() {
    }

    /**
     * Constructor with all fields.
     *
     * @param error the error type/category
     * @param message detailed error message
     * @param status HTTP status code
     */
    public ErrorResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
    }

    // Getters and Setters

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}