package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.api.ErrorResponse;
import com.nikitavbv.changewatcher.exceptions.PermissionDeniedException;
import com.nikitavbv.changewatcher.jobs.WatchingJobNotFoundException;
import com.nikitavbv.changewatcher.preview.PreviewNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomControllerAdvice {

  @ExceptionHandler(PermissionDeniedException.class)
  public ResponseEntity<ErrorResponse> handlePermissionDeniedException(PermissionDeniedException exception) {
    ErrorResponse response = new ErrorResponse("permission_denied", exception.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler(PreviewNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePreviewNotFoundException() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("preview_not_found"));
  }

  @ExceptionHandler(WatchingJobNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleWatchingJobNotFoundException() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("watching_job_not_found"));
  }

}
