package org.jjavaglue.dflib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.dflib.jjava.jupyter.kernel.display.DisplayData;

/**
 * Utility class for displaying images in Jupyter notebooks with JJava kernel.
 */
public class ImageUtil {

  /**
   * Display an image file directly in the notebook.
   *
   * @param path Path to the image file
   * @return DisplayData object that can be returned from a notebook cell
   */
  public static DisplayData displayImage(String path) {
    try {
      // Resolve file path
      File file = new File(path);
      if (!file.isAbsolute()) {
        file = new File(System.getProperty("user.dir"), path);
      }

      if (!file.exists()) {
        System.err.println("Image file not found: " + file.getAbsolutePath());
        return new DisplayData("Image not found: " + path);
      }

      // Determine image format from file extension
      String fileName = file.getName();
      String format = "png"; // Default format
      int lastDotIndex = fileName.lastIndexOf('.');
      if (lastDotIndex >= 0 && lastDotIndex < fileName.length() - 1) {
        format = fileName.substring(lastDotIndex + 1).toLowerCase();
        // Handle jpg vs jpeg
        if (format.equals("jpg")) format = "jpeg";
      }

      // Load the image
      BufferedImage image = ImageIO.read(file);
      if (image == null) {
        System.err.println("Failed to read image: " + file.getAbsolutePath());
        return new DisplayData("Failed to read image: " + path);
      }

      // Convert to base64
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ImageIO.write(image, format, byteStream);
      byte[] imageBytes = byteStream.toByteArray();
      String base64 = Base64.getEncoder().encodeToString(imageBytes);

      // Create DisplayData with the image
      DisplayData displayData = new DisplayData();
      displayData.putData("image/" + format, base64);

      return displayData;
    } catch (IOException e) {
      System.err.println("Error displaying image: " + e.getMessage());
      e.printStackTrace();
      return new DisplayData("Error: " + e.getMessage());
    }
  }

  /**
   * Display an image file with custom dimensions.
   *
   * @param path Path to the image file
   * @param width Width in pixels (use -1 to maintain aspect ratio)
   * @param height Height in pixels (use -1 to maintain aspect ratio)
   * @return DisplayData object that can be returned from a notebook cell
   */
  public static DisplayData displayImage(String path, int width, int height) {
    try {
      // Resolve file path
      File file = new File(path);
      if (!file.isAbsolute()) {
        file = new File(System.getProperty("user.dir"), path);
      }

      if (!file.exists()) {
        System.err.println("Image file not found: " + file.getAbsolutePath());
        return new DisplayData("Image not found: " + path);
      }

      // Determine image format from file extension
      String fileName = file.getName();
      String format = "png"; // Default format
      int lastDotIndex = fileName.lastIndexOf('.');
      if (lastDotIndex >= 0 && lastDotIndex < fileName.length() - 1) {
        format = fileName.substring(lastDotIndex + 1).toLowerCase();
        // Handle jpg vs jpeg
        if (format.equals("jpg")) format = "jpeg";
      }

      // Load the image
      BufferedImage originalImage = ImageIO.read(file);
      if (originalImage == null) {
        System.err.println("Failed to read image: " + file.getAbsolutePath());
        return new DisplayData("Failed to read image: " + path);
      }

      // Calculate dimensions if needed
      int originalWidth = originalImage.getWidth();
      int originalHeight = originalImage.getHeight();

      if (width == -1 && height != -1) {
        // Calculate width based on height to maintain aspect ratio
        width = (int) (originalWidth * (height / (double) originalHeight));
      } else if (height == -1 && width != -1) {
        // Calculate height based on width to maintain aspect ratio
        height = (int) (originalHeight * (width / (double) originalWidth));
      } else if (width == -1 && height == -1) {
        // Use original dimensions
        width = originalWidth;
        height = originalHeight;
      }

      // Create a HTML representation with the specified dimensions
      DisplayData displayData = new DisplayData();

      // For GIFs, we'll use an HTML approach to preserve animation
      if (format.equals("gif")) {
        // Convert to base64
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, format, byteStream);
        byte[] imageBytes = byteStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        String html = String.format(
            "<img src=\"data:image/gif;base64,%s\" width=\"%d\" height=\"%d\" />",
            base64, width, height
        );
        displayData.putHTML(html);
      } else {
        // For non-GIFs, resize the image
        java.awt.Image scaledImage = originalImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        resizedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

        // Convert to base64
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, format, byteStream);
        byte[] imageBytes = byteStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        // Set the image data
        displayData.putData("image/" + format, base64);
      }

      return displayData;
    } catch (IOException e) {
      System.err.println("Error displaying image: " + e.getMessage());
      e.printStackTrace();
      return new DisplayData("Error: " + e.getMessage());
    }
  }

  /**
   * Display an image in the notebook.
   * This method returns a DisplayData object that will render the image.
   *
   * @param path Path to the image file
   * @return DisplayData object that renders as an image when returned from a cell
   */
  public static org.dflib.jjava.jupyter.kernel.display.DisplayData showImage(String path) {
    return ImageUtil.displayImage(path);
  }

  /**
   * Display an image in the notebook with custom dimensions.
   *
   * @param path Path to the image file
   * @param width Width in pixels (use -1 to maintain aspect ratio)
   * @param height Height in pixels (use -1 to maintain aspect ratio)
   * @return DisplayData object that renders as an image when returned from a cell
   */
  public static org.dflib.jjava.jupyter.kernel.display.DisplayData showImage(String path, int width, int height) {
    return ImageUtil.displayImage(path, width, height);
  }
}
