package com.nikitavbv.changewatcher.preview;

/**
 * Thrown if requested web page preview does not exist or has not been
 * generated yet.
 *
 * @author Nikita Volobuev
 */
public class PreviewNotFoundException extends RuntimeException {

  /** Description of this error. */
  private static final String DESCRIPTION = "Preview not found";

  PreviewNotFoundException() {
    super(DESCRIPTION);
  }

  PreviewNotFoundException(Throwable e) {
    super(DESCRIPTION, e);
  }
}
