package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.api.ErrorResponse;
import com.nikitavbv.changewatcher.jobs.ScreenshotNotFoundException;
import com.nikitavbv.changewatcher.jobs.WatchingJobNotFoundException;
import com.nikitavbv.changewatcher.preview.PreviewNotFoundException;
import com.nikitavbv.changewatcher.security.AuthRequiredException;
import com.nikitavbv.changewatcher.security.PermissionDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handle exceptions thrown by other controllers.
 *
 * @author Nikita Volobuev
 */
@ControllerAdvice
public class CustomControllerAdvice {

  /** Send api permission denied exception response. */
  @ExceptionHandler(PermissionDeniedException.class)
  public ResponseEntity<ErrorResponse> handlePermissionDeniedException(
          final PermissionDeniedException exception
  ) {
    final ErrorResponse response = new ErrorResponse("permission_denied", exception.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  /** Inform client that auth is required to access this api. */
  @ExceptionHandler(AuthRequiredException.class)
  public ResponseEntity<ErrorResponse> handleAuthRequiredException() {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("auth_required"));
  }

  /** Inform client that application setup should be performed. */
  @ExceptionHandler(SetupRequiredException.class)
  public ResponseEntity<ErrorResponse> handleSetupRequiredException() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("setup_required"));
  }

  /** Inform client that requested preview does not exist. */
  @ExceptionHandler(PreviewNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePreviewNotFoundException() {
    final ErrorResponse errorResponse = new ErrorResponse("preview_not_found");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /** Inform client that requested job does not exist. */
  @ExceptionHandler(WatchingJobNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleWatchingJobNotFoundException() {
    final ErrorResponse errorResponse = new ErrorResponse("watching_job_not_found");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /** Inform client that requested screenshot does not exist. */
  @ExceptionHandler(ScreenshotNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleScreenshotNotFoundException() {
    final ErrorResponse errorResponse = new ErrorResponse("screenshot_not_found");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

}
