package com.nikitavbv.changewatcher.preview;

import com.nikitavbv.changewatcher.AbstractWebdriverThread;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 * Thread to generate web page preview screenshots.
 *
 * @author Nikita Volobuev
 */
public class GeneratePreviewThread extends AbstractWebdriverThread {

  /** Logger for this class. */
  private static final Logger LOG = Logger.getLogger(GeneratePreviewThread.class.getName());

  /** Page url to make preview of. */
  private final String url;
  /** Preview ID, or name of the file to save preview to. */
  private final String previewID;
  /** Directory where page previews are stored. */
  private final String previewsDir;

  /**
   * Creates GeneratePreviewThread.
   *
   * @param url link to page to generate preview of
   * @param previewID name of the preview file
   * @param previewsDir directory where previews are saved to
   */
  GeneratePreviewThread(final String url, final String previewID, final String previewsDir) {
    super();
    this.url = url;
    this.previewID = previewID;
    this.previewsDir = previewsDir;
  }

  /**
   * Run preview generation.
   */
  public void run() {
    WebDriver driver = getWebDriver();
    if (driver == null) {
      return;
    }
    driver.get(url);
    final Screenshot screenshot = new AShot()
      .shootingStrategy(ShootingStrategies.viewportPasting(1000))
      .takeScreenshot(driver);

    try {
      ImageIO.write(
        screenshot.getImage(),
        PreviewController.IMAGE_FORMAT.toUpperCase(Locale.getDefault()),
        getTargetFile()
      );
    } catch (IOException e) {
      LOG.log(Level.WARNING, "Failed to write screenshot to file", e);
    }
  }

  /** Returns file to save screenshot to. */
  private File getTargetFile() {
    final File targetFile = new File(
            previewsDir + previewID + "." + PreviewController.IMAGE_FORMAT
    );

    if (!targetFile.getParentFile().exists()) {
      final boolean result = targetFile.getParentFile().mkdirs();
      if (!result) {
        LOG.warning("Failed to make dirs for preview directory");
      }
    }

    if (!targetFile.exists()) {
      try {
        final boolean result = targetFile.createNewFile();
        if (!result) {
          LOG.warning("Failed to create new file for preview");
        }
      } catch(IOException e) {
        if (LOG.isLoggable(Level.WARNING)) {
          LOG.log(Level.WARNING, "Failed to create new file for preview", e);
        }
      }
    }

    return targetFile;
  }
}
