package com.nikitavbv.changewatcher.preview;

import com.nikitavbv.changewatcher.ApplicationProperties;
import com.nikitavbv.changewatcher.RouteConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * API to fetch web page previews.
 *
 * @author Nikita Volobuev
 */
@RestController
@RequestMapping(RouteConstants.PREVIEW_API)
public class PreviewController {

  /** Maximum number of threads used to generate previews. */
  private static final int MAX_PREVIEW_THREADS = 1;
  /** Directory to save preview screenshots to. */
  private static final String PREVIEWS_DIR = "previews/";
  /** Preview image format. */
  /* default */ static final String PREVIEW_IMAGE_FORMAT = "png";

  /** ApplicationProperties are required to get application data dir. */
  private final ApplicationProperties applicationProperties;
  /** Thread pool used to run preview threads. */
  private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_PREVIEW_THREADS);

  /**
   * Creates preview controller.
   *
   * @param applicationProperties application properties
   */
  public PreviewController(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  /**
   * Enqueues preview generation job.
   */
  @PostMapping
  public WebpagePreviewResponse makePreviewFor(@RequestBody WebpagePreviewRequest req) {
    final String screenshotID = UUID.randomUUID().toString();
    final GeneratePreviewThread thread = new GeneratePreviewThread(
            req.getUrl(),
            screenshotID,
            getPreviewsDirPath()
    );
    executorService.submit(thread);
    return new WebpagePreviewResponse(screenshotID);
  }

  /**
   * Returns generated preview by ID.
   */
  @GetMapping(value = "/{previewID}", produces = MediaType.IMAGE_PNG_VALUE)
  public @ResponseBody byte[] getPreview(@PathVariable String previewID) {
    final File previewFile = new File(
            getPreviewsDirPath() + previewID + "." + PREVIEW_IMAGE_FORMAT
    );
    if (!previewFile.exists()) {
      throw new PreviewNotFoundException();
    }

    try {
      return IOUtils.toByteArray(new FileInputStream(previewFile));
    } catch (IOException e) {
      throw new PreviewNotFoundException();
    }
  }

  /** Returns previews dir. */
  private String getPreviewsDirPath() {
    return applicationProperties.getDataDir() + PREVIEWS_DIR;
  }
}
