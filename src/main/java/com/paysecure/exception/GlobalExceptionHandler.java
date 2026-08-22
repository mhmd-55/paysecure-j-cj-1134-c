package com.paysecure.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

/**
 * FINDING 10 (Part J) — REMEDIATED.
 * - ResponseStatusException (used deliberately by Findings 5, 6, 9 for clean
 *   401/403/404 responses) still returns its intended status and message.
 * - Every other, genuinely unexpected exception is logged with full detail
 *   SERVER-SIDE ONLY, and the client receives a generic message plus a
 *   correlation ID they can quote to support - never the stack trace itself.
 * Mohammad Ismail CJ-1134-C
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("error", String.valueOf(ex.getReason())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        String referenceId = UUID.randomUUID().toString();
        log.error("Unhandled exception [ref={}]", referenceId, exception);
        return ResponseEntity.status(500)
                .body(Map.of("error", "An internal error occurred. Please contact support.", "reference", referenceId));
    }
}