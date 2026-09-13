package com.arvince.tasktracker.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Consistent JSON error body returned for every error response.
 */
public record ErrorResponse(
		LocalDateTime timestamp,
		int status,
		String error,
		String message,
		String path,
		Map<String, String> fieldErrors
) {

	public static ErrorResponse of(int status, String error, String message, String path) {
		return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
	}

	public static ErrorResponse of(int status, String error, String message, String path, Map<String, String> fieldErrors) {
		return new ErrorResponse(LocalDateTime.now(), status, error, message, path, fieldErrors);
	}
}
