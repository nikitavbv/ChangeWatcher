package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.api.ErrorResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handle unhandled errors.
 *
 * @author Nikita Volobuev
 */
@RestController
public class IndexController implements ErrorController {

  /** Attribute of HttpServletRequest containing error status code. */
  private static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
  /** Attribute of HttpServletRequest containing error message. */
  private static final String ERROR_MESSAGE = "javax.servlet.error.message";
  /** Status of error page. */
  private static final String ERROR_PATH = "/error";

  /**
   * Processes errors from other controllers.
   *
   * <p>Error message is returned as body.
   * If no matching controller was found for route, this returns frontend
   * index.html instead of 404 error.</p>
   */
  @RequestMapping(ERROR_PATH)
  public Object error(final HttpServletRequest req) {
    final int errorStatusCode = ((Integer) req.getAttribute(ERROR_STATUS_CODE));

    if (errorStatusCode == HttpStatus.NOT_FOUND.value()) {
      return new ModelAndView("index.html");
    }

    final String errorMessage = ((String) req.getAttribute(ERROR_MESSAGE));
    return ResponseEntity.status(errorStatusCode).body(new ErrorResponse("error", errorMessage));
  }

  /** Returns path of the error page. */
  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

}
