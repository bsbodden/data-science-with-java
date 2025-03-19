package org.jjavaglue.core;

import java.awt.*;
import java.awt.image.BufferedImage;

public class V {

  public static BufferedImage visualizeEmbedding(String embeddingName, float[] embedding, int cellWidth, int cellHeight,
      boolean printValues) {
    int size = embedding.length;
    int imageWidth = cellWidth * size;

    // Adding some space for the name and values
    int padding = 50;
    int imageHeight = cellHeight + padding;

    BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();

    // Set the font and draw the embedding name
    g.setFont(new Font("Arial", Font.BOLD, 16));
    FontMetrics metrics = g.getFontMetrics();
    g.drawString(embeddingName, 5, metrics.getHeight());

    // Find min and max to adapt the normalization
    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    for (float value : embedding) {
      min = Math.min(min, value);
      max = Math.max(max, value);
    }

    System.out.println("Embedding range: [" + min + ", " + max + "]");

    for (int i = 0; i < size; i++) {
      float value = embedding[i];

      // Normalize based on actual range instead of fixed range
      float normalizedValue = (value - min) / (max - min);

      // Linearly interpolate between blue and white for values below 0.5 and between white and red for values above 0.5
      float red = normalizedValue > 0.5 ? 1 : 2 * normalizedValue;
      float green = normalizedValue > 0.5 ? 2 * (1 - normalizedValue) : 2 * normalizedValue;
      float blue = normalizedValue > 0.5 ? 2 * (1 - normalizedValue) : 1;

      Color color = new Color(red, green, blue);

      for (int h = padding; h < imageHeight; h++) {
        for (int w = i * cellWidth; w < (i + 1) * cellWidth; w++) {
          image.setRGB(w, h, color.getRGB());
        }
      }

      // If printValues is true, print the value of each cell
      if (printValues) {
        g.setColor(Color.BLACK);
        g.drawString(String.format("%.4f", value), i * cellWidth, imageHeight - 5);
      }
    }

    g.dispose();

    return image;
  }
}
