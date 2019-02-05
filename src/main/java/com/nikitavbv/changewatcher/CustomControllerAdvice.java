package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.api.ErrorResponse;
import com.nikitavbv.changewatcher.exceptions.PermissionDeniedException;
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

}
