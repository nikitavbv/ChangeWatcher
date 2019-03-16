package com.nikitavbv.changewatcher.preview;

/**
 * Thrown if requested web page preview does not exist or has not been
 * generated yet.
 *
 * @author Nikita Volobuev
 */
public class PreviewNotFoundException extends RuntimeException {

  PreviewNotFoundException() {
    super("Preview not found");
  }

}
