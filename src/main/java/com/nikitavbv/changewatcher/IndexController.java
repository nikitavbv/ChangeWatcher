package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.api.ErrorResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController implements ErrorController {

  private static final String ERROR_STATUS_CODE_ATTRIBUTE = "javax.servlet.error.status_code";
  private static final String ERROR_MESSAGE_ATTRIBUTE = "javax.servlet.error.message";
  private static final String ERROR_PATH = "/error";

  /**
   * Processes errors from other controllers.
   *
   * Error message is returned as body.
   * If no matching controller was found for route, this returns frontend index.html instead of
   * 404 error.
   */
  @RequestMapping(ERROR_PATH)
  public Object error(HttpServletRequest req) {
    int errorStatusCode = ((Integer) req.getAttribute(ERROR_STATUS_CODE_ATTRIBUTE));

    if (errorStatusCode == HttpStatus.NOT_FOUND.value()) {
      return new ModelAndView("index.html");
    }

    String errorMessage = ((String) req.getAttribute(ERROR_MESSAGE_ATTRIBUTE));
    return ResponseEntity.status(errorStatusCode).body(new ErrorResponse("error", errorMessage));
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

}
