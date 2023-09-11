package org.example.exception;

/**
 * A custom Class hor handling exceptions.
 */
public class AuditException extends RuntimeException {
    public AuditException(String message) {
        super(message);
    }
}