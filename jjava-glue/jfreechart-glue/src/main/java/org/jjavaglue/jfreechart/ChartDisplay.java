package org.jjavaglue.jfreechart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Utility class for displaying JFreeChart charts in Jupyter notebooks.
 */
public class ChartDisplay {

  // Default width and height for chart images
  private static final int DEFAULT_WIDTH = 800;
  private static final int DEFAULT_HEIGHT = 600;

  /**
   * Convert a JFreeChart to a BufferedImage for direct display in Jupyter
   *
   * @param chart The JFreeChart to convert
   * @return BufferedImage that can be returned directly from a Jupyter cell
   */
  public static BufferedImage toImage(JFreeChart chart) {
    return toImage(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /**
   * Convert a JFreeChart to a BufferedImage for direct display in Jupyter
   *
   * @param chart  The JFreeChart to convert
   * @param width  The width of the resulting image
   * @param height The height of the resulting image
   * @return BufferedImage that can be returned directly from a Jupyter cell
   */
  public static BufferedImage toImage(JFreeChart chart, int width, int height) {
    if (chart == null) {
      // Create an error image
      BufferedImage errorImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = errorImg.createGraphics();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, width, height);
      g2d.setColor(Color.RED);
      g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
      g2d.drawString("Error: Chart is null", 10, height / 2);
      g2d.dispose();
      return errorImg;
    }

    return chart.createBufferedImage(width, height);
  }

  /**
   * Convert a ChartPanel to a BufferedImage for direct display in Jupyter
   *
   * @param panel The ChartPanel to convert
   * @return BufferedImage that can be returned directly from a Jupyter cell
   */
  public static BufferedImage toImage(ChartPanel panel) {
    if (panel == null) {
      return createErrorImage("Error: Chart panel is null", DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    JFreeChart chart = panel.getChart();
    if (chart == null) {
      return createErrorImage("Error: Chart in panel is null", DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    // Use preferred size if available, otherwise use defaults
    Dimension prefSize = panel.getPreferredSize();
    int width = (prefSize != null && prefSize.width > 0) ? prefSize.width : DEFAULT_WIDTH;
    int height = (prefSize != null && prefSize.height > 0) ? prefSize.height : DEFAULT_HEIGHT;

    return toImage(chart, width, height);
  }

  /**
   * Convert a JPanel containing charts to a BufferedImage for direct display in Jupyter
   *
   * @param panel The JPanel to convert (such as a pairplot grid)
   * @return BufferedImage that can be returned directly from a Jupyter cell
   */
  public static BufferedImage toImage(JPanel panel) {
    if (panel == null) {
      return createErrorImage("Error: Panel is null", DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    Dimension prefSize = panel.getPreferredSize();
    int width = (prefSize != null && prefSize.width > 0) ? prefSize.width : DEFAULT_WIDTH;
    int height = (prefSize != null && prefSize.height > 0) ? prefSize.height : DEFAULT_HEIGHT;

    return toImage(panel, width, height);
  }

  /**
   * Convert a JPanel containing charts to a BufferedImage for direct display in Jupyter
   *
   * @param panel  The JPanel to convert (such as a pairplot grid)
   * @param width  The width of the resulting image
   * @param height The height of the resulting image
   * @return BufferedImage that can be returned directly from a Jupyter cell
   */
  public static BufferedImage toImage(JPanel panel, int width, int height) {
    if (panel == null) {
      return createErrorImage("Error: Panel is null", width, height);
    }

    // Create a BufferedImage to render the panel
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();

    // Set high quality rendering
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    // Fill with white background (in case panel has transparency)
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, width, height);

    // Ensure panel is sized correctly
    panel.setSize(width, height);

    // Render the panel to the image
    panel.paint(g2d);
    g2d.dispose();

    return image;
  }

  /**
   * Create an error image with a message
   */
  private static BufferedImage createErrorImage(String message, int width, int height) {
    BufferedImage errorImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = errorImg.createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, width, height);
    g2d.setColor(Color.RED);
    g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
    g2d.drawString(message, 10, height / 2);
    g2d.dispose();
    return errorImg;
  }

  // Keep HTML methods for backward compatibility

  /**
   * Convert a JFreeChart to HTML for display in a Jupyter notebook
   *
   * @param chart The JFreeChart to display
   * @return HTML img tag with the chart as a base64-encoded PNG
   */
  public static String toHTML(JFreeChart chart) {
    return toHTML(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /**
   * Convert a JFreeChart to HTML for display in a Jupyter notebook
   *
   * @param chart  The JFreeChart to display
   * @param width  The width of the resulting image
   * @param height The height of the resulting image
   * @return HTML img tag with the chart as a base64-encoded PNG
   */
  public static String toHTML(JFreeChart chart, int width, int height) {
    if (chart == null) {
      return "<p>Error: Chart is null</p>";
    }

    try {
      String base64 = chartToBase64(chart, width, height);
      return "<img src=\"data:image/png;base64," + base64 + "\" width=\"" + width + "\" height=\"" + height + "\">";
    } catch (IOException e) {
      return "<p>Error creating chart image: " + e.getMessage() + "</p>";
    }
  }

  /**
   * Convert a ChartPanel to HTML for display in a Jupyter notebook
   *
   * @param panel The ChartPanel to display
   * @return HTML img tag with the chart as a base64-encoded PNG
   */
  public static String toHTML(ChartPanel panel) {
    if (panel == null) {
      return "<p>Error: Chart panel is null</p>";
    }

    JFreeChart chart = panel.getChart();
    if (chart == null) {
      return "<p>Error: Chart in panel is null</p>";
    }

    // Use preferred size if available, otherwise use defaults
    Dimension prefSize = panel.getPreferredSize();
    int width = (prefSize != null && prefSize.width > 0) ? prefSize.width : DEFAULT_WIDTH;
    int height = (prefSize != null && prefSize.height > 0) ? prefSize.height : DEFAULT_HEIGHT;

    return toHTML(chart, width, height);
  }

  /**
   * Convert a JPanel containing charts to HTML for display in a Jupyter notebook
   *
   * @param panel The JPanel to display (such as a pairplot grid)
   * @return HTML img tag with the panel as a base64-encoded PNG
   */
  public static String toHTML(JPanel panel) {
    if (panel == null) {
      return "<p>Error: Panel is null</p>";
    }

    Dimension prefSize = panel.getPreferredSize();
    int width = (prefSize != null && prefSize.width > 0) ? prefSize.width : DEFAULT_WIDTH;
    int height = (prefSize != null && prefSize.height > 0) ? prefSize.height : DEFAULT_HEIGHT;

    return toHTML(panel, width, height);
  }

  /**
   * Convert a JPanel containing charts to HTML for display in a Jupyter notebook
   *
   * @param panel  The JPanel to display (such as a pairplot grid)
   * @param width  The width of the resulting image
   * @param height The height of the resulting image
   * @return HTML img tag with the panel as a base64-encoded PNG
   */
  public static String toHTML(JPanel panel, int width, int height) {
    if (panel == null) {
      return "<p>Error: Panel is null</p>";
    }

    try {
      BufferedImage image = toImage(panel, width, height);

      // Convert the image to base64
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

      return "<img src=\"data:image/png;base64," + base64 + "\" width=\"" + width + "\" height=\"" + height + "\">";
    } catch (IOException e) {
      return "<p>Error creating panel image: " + e.getMessage() + "</p>";
    }
  }

  /**
   * Special method to render a pairplot panel to an image.
   * This handles sizing and layout of grid components properly.
   *
   * @param panel  The JPanel containing the pairplot grid
   * @param width  Width of the resulting image
   * @param height Height of the resulting image
   * @return BufferedImage of the rendered pairplot
   */
  public static BufferedImage toPairplotImage(JPanel panel, int width, int height) {
    // Don't create a new panel, work with the existing one
    panel.setSize(width, height);
    panel.setPreferredSize(new Dimension(width, height));

    // Calculate the grid size
    int gridSize = (int) Math.sqrt(panel.getComponentCount());
    int cellSize = width / gridSize;

    // Define consistent colors for all plots
    // Use the darker gray background from the KDE plots
    Color bgColor = new Color(232, 232, 232);  // Darker gray background from KDE plots
    Color gridColor = Color.WHITE;             // White grid lines

    // Ensure all components have proper size and consistent styling
    for (int i = 0; i < panel.getComponentCount(); i++) {
      Component comp = panel.getComponent(i);
      comp.setSize(cellSize, cellSize);
      comp.setPreferredSize(new Dimension(cellSize, cellSize));

      // If the component is a ChartPanel, apply consistent styling
      if (comp instanceof ChartPanel chartPanel) {
        // Add border to separate plots visually
        chartPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Apply consistent styling to all charts
        JFreeChart chart = chartPanel.getChart();
        if (chart.getPlot() instanceof XYPlot plot) {
          // FORCE the same background and grid colors for ALL plots
          plot.setBackgroundPaint(bgColor);
          plot.setDomainGridlinePaint(gridColor);
          plot.setRangeGridlinePaint(gridColor);

          // Add thin borders
          plot.setOutlinePaint(new Color(220, 220, 220));
          plot.setOutlineStroke(new BasicStroke(0.5f));
        }
      }
    }

    // Rest of the method remains unchanged
    panel.validate();
    panel.doLayout();

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, width, height);

    panel.paint(g2d);
    g2d.dispose();

    return image;
  }

  /**
   * Convert a JFreeChart to a base64-encoded PNG image
   *
   * @param chart  The chart to convert
   * @param width  The width of the resulting image
   * @param height The height of the resulting image
   * @return Base64-encoded PNG image
   * @throws IOException If an error occurs during image creation
   */
  private static String chartToBase64(JFreeChart chart, int width, int height) throws IOException {
    BufferedImage image = chart.createBufferedImage(width, height);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", baos);
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }
}