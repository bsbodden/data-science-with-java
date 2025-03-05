package org.jjavaglue.jfreechart;

import org.dflib.DataFrame;
import org.dflib.GroupBy;
import org.dflib.Hasher;
import org.dflib.Series;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.List;

/**
 * Plotting utilities for DFLib DataFrames using JFreeChart.
 * Provides a Python Matplotlib/Seaborn-like interface for creating charts.
 */
public class Plots {

  // Default size for individual charts
  private static final int DEFAULT_WIDTH = 800;
  private static final int DEFAULT_HEIGHT = 600;

  private static final Color[] COLORS = { //
      new Color(0xE24A33), // red
      new Color(0x348ABD), // blue
      new Color(0x988ED5), // purple
      new Color(0x777777), // gray
      new Color(0xFBC15E), // yellow
      new Color(0x8EBA42),  // green
  };

  // Current style/theme
  private static String currentStyle = "default";

  /**
   * Set the plot style (theme)
   *
   * @param style Style name ("default", "ggplot", "dark", etc.)
   */
  public static void style(String style) {
    currentStyle = style;
    // Apply style settings (font, colors, etc.)
    applyStyle(style);
  }

  /**
   * Apply style settings to JFreeChart
   */
  private static void applyStyle(String style) {
    // TODO: Implement different style settings
    // This would modify default colors, fonts, backgrounds, etc.
  }

  /**
   * Create a scatter plot similar to matplotlib's scatter function
   *
   * @param df  The DataFrame containing the data
   * @param x   Column name for x-axis values
   * @param y   Column name for y-axis values
   * @param hue Column name for coloring points (optional, can be null)
   * @return A ChartPanel containing the scatter plot
   */
  public static ChartPanel scatter(DataFrame df, String x, String y, String hue) {
    // Create dataset
    XYSeriesCollection dataset = new XYSeriesCollection();

    if (hue != null && !hue.isEmpty()) {
      // Group by the hue column and create a series for each group
      GroupBy groupedData = df.group(Hasher.of(hue));

      for (Object category : groupedData.getGroupKeys()) {
        DataFrame categoryDf = groupedData.getGroup(category);
        XYSeries series = new XYSeries(category.toString());

        Series<?> xValues = categoryDf.getColumn(x);
        Series<?> yValues = categoryDf.getColumn(y);

        for (int i = 0; i < categoryDf.height(); i++) {
          double xVal = Double.parseDouble(xValues.get(i).toString());
          double yVal = Double.parseDouble(yValues.get(i).toString());
          series.add(xVal, yVal);
        }

        dataset.addSeries(series);
      }
    } else {
      // No grouping, create a single series
      XYSeries series = new XYSeries(y + " vs " + x);

      Series<?> xValues = df.getColumn(x);
      Series<?> yValues = df.getColumn(y);

      for (int i = 0; i < df.height(); i++) {
        double xVal = Double.parseDouble(xValues.get(i).toString());
        double yVal = Double.parseDouble(yValues.get(i).toString());
        series.add(xVal, yVal);
      }

      dataset.addSeries(series);
    }

    // Create the chart
    JFreeChart chart = createScatterPlot(dataset, x, y);

    // Style the chart based on the current style
    styleChart(chart);

    // Create and return the chart panel with a proper size
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    return chartPanel;
  }

  /**
   * Create a histogram similar to matplotlib's hist function
   *
   * @param df     The DataFrame containing the data
   * @param column Column name for the values to histogram
   * @param bins   Number of bins (default 10)
   * @return A ChartPanel containing the histogram
   */
  public static ChartPanel histogram(DataFrame df, String column, int bins) {
    // Extract values from the DataFrame column
    Series<?> values = df.getColumn(column);
    double[] data = new double[values.size()];

    for (int i = 0; i < values.size(); i++) {
      data[i] = Double.parseDouble(values.get(i).toString());
    }

    // Create the dataset
    HistogramDataset dataset = new HistogramDataset();
    dataset.addSeries(column, data, bins);

    // Create the chart
    JFreeChart chart = createHistogram(dataset, column);

    // Style the chart based on the current style
    styleChart(chart);

    // Create and return the chart panel with a proper size
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    return chartPanel;
  }

  /**
   * Create a histogram with default number of bins (10)
   */
  public static ChartPanel histogram(DataFrame df, String column) {
    return histogram(df, column, 10);
  }

  /**
   * Create a pairplot similar to seaborn's pairplot function
   *
   * @param df      The DataFrame containing the data
   * @param hue     Column name for coloring points (optional, can be null)
   * @param columns Columns to include in the pairplot
   * @return A JPanel containing the grid of scatter plots
   */
  public static JPanel pairplot(DataFrame df, String hue, String... columns) {
    // If no columns specified, find all numeric columns
    if (columns.length == 0) {
      List<String> numericCols = new ArrayList<>();
      for (String col : df.getColumnsIndex()) {
        if (!col.equals(hue) && isNumeric(df.getColumn(col))) {
          numericCols.add(col);
        }
      }
      columns = numericCols.toArray(new String[0]);
    }

    int n = columns.length;
    if (n == 0) {
      return new JPanel(); // Empty panel if no columns
    }

    // Create a grid panel for the scatter plots
    JPanel panel = new JPanel(new GridLayout(n, n, 2, 2));
    panel.setBackground(Color.WHITE);

    // Calculate appropriate cell size
    int cellSize = 250; // Fixed size for each cell
    int totalSize = cellSize * n;

    panel.setPreferredSize(new Dimension(totalSize, totalSize));
    panel.setMinimumSize(new Dimension(totalSize, totalSize));
    panel.setSize(totalSize, totalSize);

    // Create a category-to-colorindex mapping from the hue column
    Map<Object, Integer> categoryColorMap = new HashMap<>();
    if (hue != null && !hue.isEmpty()) {
      Series<?> hueSeries = df.getColumn(hue);
      Set<Object> uniqueCategories = new LinkedHashSet<>();
      for (int i = 0; i < hueSeries.size(); i++) {
        uniqueCategories.add(hueSeries.get(i));
      }

      int colorIdx = 0;
      for (Object category : uniqueCategories) {
        categoryColorMap.put(category, colorIdx);
        colorIdx++;
      }
    }

    // Create plots for each pair of columns
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        String xCol = columns[j];
        String yCol = columns[i];

        // Determine if this is an edge panel (needs axis labels)
        boolean showXLabel = (i == n - 1); // Bottom row
        boolean showYLabel = (j == 0);   // First column

        if (i == j) {
          // Diagonal: Create KDE plots
          ChartPanel kdePanel = kdeplot(df, xCol, hue, categoryColorMap);

          // Adjust axis labels visibility
          adjustAxisLabels(kdePanel.getChart(), showXLabel, showYLabel, xCol, yCol);

          kdePanel.setPreferredSize(new Dimension(cellSize, cellSize));
          kdePanel.setMinimumSize(new Dimension(cellSize, cellSize));
          panel.add(kdePanel);
        } else {
          // Off-diagonal: Create scatter plots
          ChartPanel scatterPanel = createMinimalScatterPlot(df, xCol, yCol, hue, categoryColorMap);

          // Adjust axis labels visibility
          adjustAxisLabels(scatterPanel.getChart(), showXLabel, showYLabel, xCol, yCol);

          scatterPanel.setPreferredSize(new Dimension(cellSize, cellSize));
          scatterPanel.setMinimumSize(new Dimension(cellSize, cellSize));
          panel.add(scatterPanel);
        }
      }
    }

    panel.revalidate();
    return panel;
  }

  /**
   * Create a bar plot similar to matplotlib's bar function
   *
   * @param df The DataFrame containing the data
   * @param x  Column name for x-axis categories
   * @param y  Column name for y-axis values
   * @return A ChartPanel containing the bar plot
   */
  public static ChartPanel barplot(DataFrame df, String x, String y) {
    // Create the dataset
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    Series<?> xValues = df.getColumn(x);
    Series<?> yValues = df.getColumn(y);

    for (int i = 0; i < df.height(); i++) {
      String category = xValues.get(i).toString();
      double value = Double.parseDouble(yValues.get(i).toString());
      dataset.addValue(value, y, category);
    }

    // Create the chart
    JFreeChart chart = createBarChart(dataset, x, y);

    // Style the chart based on the current style
    styleChart(chart);

    // Create and return the chart panel with a proper size
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    return chartPanel;
  }

  // KDE

  /**
   * Create a density plot similar to seaborn's kdeplot
   *
   * @param df               The DataFrame containing the data
   * @param column           Column name for the values to plot
   * @param hue              Column name for coloring by category (optional, can be null)
   * @param categoryColorMap Map of category values to color indices
   * @return A ChartPanel containing the KDE plot
   */
  public static ChartPanel kdeplot(DataFrame df, String column, String hue, Map<Object, Integer> categoryColorMap) {
    JFreeChart chart;

    if (hue != null && !hue.isEmpty()) {
      // Group by the hue column and create a series for each group
      GroupBy groupedData = df.group(Hasher.of(hue));
      XYSeriesCollection dataset = new XYSeriesCollection();

      for (Object category : groupedData.getGroupKeys()) {
        DataFrame categoryDf = groupedData.getGroup(category);
        Series<?> values = categoryDf.getColumn(column);

        // Calculate KDE for this group
        double[] data = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
          data[i] = Double.parseDouble(values.get(i).toString());
        }

        // Create KDE series
        XYSeries series = createKDESeries(data, category.toString());
        dataset.addSeries(series);
      }

      // Create the chart
      chart = createLineChart(dataset, column, "Density", categoryColorMap);

    } else {
      // No grouping, create a single KDE
      Series<?> values = df.getColumn(column);
      double[] data = new double[values.size()];

      for (int i = 0; i < values.size(); i++) {
        data[i] = Double.parseDouble(values.get(i).toString());
      }

      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries series = createKDESeries(data, column);
      dataset.addSeries(series);

      // Since there's no hue, we'll use an empty map for colors
      chart = createLineChart(dataset, column, "Density", new HashMap<>());
    }

    // Remove title and legend for cleaner look in pairplot
    chart.setTitle((TextTitle) null);
    chart.removeLegend();

    // Style the chart
    styleChart(chart);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    chartPanel.setMinimumSize(new Dimension(250, 250));
    return chartPanel;
  }

  /**
   * Create a line chart for KDE plots
   */
  private static JFreeChart createLineChart(XYSeriesCollection dataset, String xLabel, String yLabel,
      Map<Object, Integer> categoryColorMap) {
    NumberAxis xAxis = new NumberAxis(xLabel);
    NumberAxis yAxis = new NumberAxis(yLabel);

    // Disable auto-ranging on both axes
    xAxis.setAutoRange(false);
    yAxis.setAutoRange(false);

    // Find the appropriate x-axis range from the dataset
    double xMin = Double.MAX_VALUE;
    double xMax = Double.MIN_VALUE;

    for (int s = 0; s < dataset.getSeriesCount(); s++) {
      XYSeries series = dataset.getSeries(s);
      for (int i = 0; i < series.getItemCount(); i++) {
        double x = series.getX(i).doubleValue();
        xMin = Math.min(xMin, x);
        xMax = Math.max(xMax, x);
      }
    }

    // Set the x-axis range explicitly
    xAxis.setRange(xMin, xMax);

    // Create plot with dataset and axes
    XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

    // Use XYAreaRenderer for filled KDE plots
    XYAreaRenderer renderer = new XYAreaRenderer();
    plot.setRenderer(renderer);

    // Set colors for each series
    for (int i = 0; i < dataset.getSeriesCount(); i++) {
      Object category = dataset.getSeriesKey(i);
      // Use the provided color mapping if available, otherwise use series index
      int colorIdx = categoryColorMap.getOrDefault(category, i) % COLORS.length;
      Color color = COLORS[colorIdx];

      renderer.setSeriesPaint(i, color);
      renderer.setSeriesFillPaint(i, color);
    }

    // Enable use of fill paint
    renderer.setUseFillPaint(true);

    // Find the maximum density value across all series for y-axis scaling
    double maxDensity = 0.0;
    for (int s = 0; s < dataset.getSeriesCount(); s++) {
      XYSeries series = dataset.getSeries(s);
      for (int i = 0; i < series.getItemCount(); i++) {
        double y = series.getY(i).doubleValue();
        maxDensity = Math.max(maxDensity, y);
      }
    }

    // Calculate an appropriate upper bound for the y-axis
    double yMax = maxDensity * 1.05;
    yAxis.setRange(0, yMax);

    // Style the plot
    // EXPLICITLY SET ALL PLOT STYLING - Exactly match createMinimalScatterPlot
    Color bgColor = new Color(245, 245, 245);
    Color gridColor = Color.WHITE;

    plot.setBackgroundPaint(bgColor);
    plot.setDomainGridlinePaint(gridColor);
    plot.setRangeGridlinePaint(gridColor);
    plot.setDomainGridlineStroke(new BasicStroke(1.0f));
    plot.setRangeGridlineStroke(new BasicStroke(1.0f));
    plot.setOutlinePaint(Color.GRAY);
    plot.setOutlineStroke(new BasicStroke(0.5f));

    JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    return chart;
  }

  /**
   * Create an XY series with KDE (Kernel Density Estimation) data
   * with improved horizontal scaling
   *
   * @param data The data to compute KDE for
   * @param name Name for the series
   * @return XYSeries with the KDE curve
   */
  private static XYSeries createKDESeries(double[] data, String name) {
    XYSeries series = new XYSeries(name);

    // Find min and max values
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for (double val : data) {
      min = Math.min(min, val);
      max = Math.max(max, val);
    }

    // Original range
    double origMin = min;
    double origMax = max;
    double range = max - min;

    // Add a buffer around the min/max
    min -= range * 0.2;
    max += range * 0.2;

    // Create evaluation points
    int numPoints = 150;
    double step = (max - min) / (numPoints - 1);

    // For each evaluation point, compute the KDE value
    double maxDensity = 0.0;
    for (int i = 0; i < numPoints; i++) {
      double x = min + (i * step);
      double density = computeKDE(x, data);
      maxDensity = Math.max(maxDensity, density);
      series.add(x, density);
    }

    return series;
  }

  /**
   * NEW METHOD: Compute the kernel density estimate
   */
  private static double computeKDE(double x, double[] data) {
    // Use Silverman's rule of thumb for bandwidth
    double h = 1.06 * computeStandardDeviation(data) * Math.pow(data.length, -0.2);

    // Apply Gaussian kernel
    double sum = 0.0;
    for (double xi : data) {
      sum += gaussian((x - xi) / h);
    }

    return sum / (data.length * h);
  }

  /**
   * NEW METHOD: Compute standard deviation
   */
  private static double computeStandardDeviation(double[] data) {
    if (data.length <= 1) {
      return 1.0; // Default if not enough data
    }

    double mean = 0.0;
    for (double val : data) {
      mean += val;
    }
    mean /= data.length;

    double variance = 0.0;
    for (double val : data) {
      double diff = val - mean;
      variance += diff * diff;
    }
    variance /= (data.length - 1);

    return Math.sqrt(variance);
  }

  /**
   * Gaussian kernel function
   */
  private static double gaussian(double x) {
    return Math.exp(-0.5 * x * x) / Math.sqrt(2.0 * Math.PI);
  }

  /**
   * Create a minimal scatter plot for use in pairplots
   * (minimal axes, grid background, circular markers)
   *
   * @param df               The DataFrame containing the data
   * @param x                Column name for x-axis values
   * @param y                Column name for y-axis values
   * @param hue              Column name for coloring points (optional, can be null)
   * @param categoryColorMap Map of category values to color indices
   * @return A ChartPanel containing the scatter plot
   */
  private static ChartPanel createMinimalScatterPlot(DataFrame df, String x, String y, String hue,
      Map<Object, Integer> categoryColorMap) {
    // Create datasets for each category
    Map<Object, XYSeriesCollection> categoryDatasets = new HashMap<>();

    if (hue != null && !hue.isEmpty()) {
      // Group by hue variable
      GroupBy groupedData = df.group(Hasher.of(hue));

      for (Object category : groupedData.getGroupKeys()) {
        DataFrame categoryDf = groupedData.getGroup(category);
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(category.toString());

        Series<?> xValues = categoryDf.getColumn(x);
        Series<?> yValues = categoryDf.getColumn(y);

        for (int i = 0; i < categoryDf.height(); i++) {
          double xVal = Double.parseDouble(xValues.get(i).toString());
          double yVal = Double.parseDouble(yValues.get(i).toString());
          series.add(xVal, yVal);
        }

        dataset.addSeries(series);
        categoryDatasets.put(category, dataset);
      }
    } else {
      // No grouping, create a single series
      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries series = new XYSeries("Points");

      Series<?> xValues = df.getColumn(x);
      Series<?> yValues = df.getColumn(y);

      for (int i = 0; i < df.height(); i++) {
        double xVal = Double.parseDouble(xValues.get(i).toString());
        double yVal = Double.parseDouble(yValues.get(i).toString());
        series.add(xVal, yVal);
      }

      dataset.addSeries(series);
      categoryDatasets.put("default", dataset);
    }

    // Create axes
    NumberAxis xAxis = new NumberAxis(x);
    NumberAxis yAxis = new NumberAxis(y);
    xAxis.setAutoRangeIncludesZero(false);
    yAxis.setAutoRangeIncludesZero(false);

    // Create the plot
    XYPlot plot = new XYPlot();
    plot.setDomainAxis(xAxis);
    plot.setRangeAxis(yAxis);

    // EXPLICITLY SET ALL PLOT STYLING
    // Set a specific light gray background color
    Color bgColor = new Color(245, 245, 245);
    Color gridColor = Color.WHITE;

    plot.setBackgroundPaint(bgColor);
    plot.setDomainGridlinePaint(gridColor);
    plot.setRangeGridlinePaint(gridColor);
    plot.setDomainGridlineStroke(new BasicStroke(1.0f));
    plot.setRangeGridlineStroke(new BasicStroke(1.0f));
    plot.setOutlinePaint(Color.GRAY);
    plot.setOutlineStroke(new BasicStroke(0.5f));

    // Add each category dataset to the plot with appropriate renderer
    int categoryIndex = 0;
    for (Map.Entry<Object, XYSeriesCollection> entry : categoryDatasets.entrySet()) {
      Object category = entry.getKey();
      XYSeriesCollection dataset = entry.getValue();

      // Use XYLineAndShapeRenderer to have more control over the shapes
      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);

      // Use the color mapping to ensure consistent colors
      int colorIdx = categoryColorMap.getOrDefault(category, categoryIndex) % COLORS.length;
      Color color = COLORS[colorIdx];
      renderer.setSeriesPaint(0, color);

      // Set circle shape with white border
      Shape circle = new Ellipse2D.Double(-3.0, -3.0, 8.0, 8.0);
      renderer.setSeriesShape(0, circle);
      renderer.setSeriesOutlinePaint(0, Color.WHITE);
      renderer.setSeriesOutlineStroke(0, new BasicStroke(0.5f));
      renderer.setUseOutlinePaint(true);

      // Add the dataset and renderer to the plot
      plot.setDataset(categoryIndex, dataset);
      plot.setRenderer(categoryIndex, renderer);

      categoryIndex++;
    }

    // Create the chart without title or legend
    JFreeChart chart = new JFreeChart(plot);
    chart.removeLegend();
    chart.setBackgroundPaint(Color.WHITE);

    // Create the chart panel with fixed size
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(250, 250));
    chartPanel.setMinimumSize(new Dimension(250, 250));

    return chartPanel;
  }

  // Helper methods for creating various chart types

  private static JFreeChart createScatterPlot(XYSeriesCollection dataset, String xLabel, String yLabel) {
    NumberAxis xAxis = new NumberAxis(xLabel);
    NumberAxis yAxis = new NumberAxis(yLabel);

    XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

    // Use a renderer that shows shapes but not lines
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);

    // Set colors for each series
    int seriesCount = dataset.getSeriesCount();
    for (int i = 0; i < seriesCount; i++) {
      renderer.setSeriesPaint(i, COLORS[i % COLORS.length]);
      renderer.setSeriesShape(i, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
    }

    plot.setRenderer(renderer);

    // Style the plot with grid lines
    plot.setBackgroundPaint(new Color(245, 245, 245)); // Light gray background
    plot.setDomainGridlinePaint(Color.WHITE);
    plot.setRangeGridlinePaint(Color.WHITE);
    plot.setDomainGridlineStroke(new BasicStroke(1.0f));
    plot.setRangeGridlineStroke(new BasicStroke(1.0f));
    plot.setOutlinePaint(Color.GRAY);
    plot.setOutlineStroke(new BasicStroke(0.5f));

    // Show legend if multiple series

    return new JFreeChart(yLabel + " vs " + xLabel, JFreeChart.DEFAULT_TITLE_FONT, plot, seriesCount > 1);
  }

  private static JFreeChart createHistogram(HistogramDataset dataset, String columnName) {
    String plotTitle = "Histogram of " + columnName;
    String yAxisLabel = "Frequency";

    JFreeChart chart = org.jfree.chart.ChartFactory.createHistogram(plotTitle, columnName, yAxisLabel, dataset,
        PlotOrientation.VERTICAL, true, true, false);

    // Style the plot
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setBackgroundPaint(Color.WHITE);
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

    // Get the renderer and customize it for a flat look
    XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setShadowVisible(false);
    renderer.setBarPainter(new StandardXYBarPainter()); // Use standard/flat painter instead of gradient
    renderer.setSeriesPaint(0, COLORS[0]);
    renderer.setDrawBarOutline(false);
    renderer.setMargin(0.1); // Adjust the bar width (0.0-1.0)

    // Set histogram color
    plot.getRenderer().setSeriesPaint(0, COLORS[0]);

    return chart;
  }

  private static JFreeChart createBarChart(DefaultCategoryDataset dataset, String xLabel, String yLabel) {
    JFreeChart chart = org.jfree.chart.ChartFactory.createBarChart(yLabel + " by " + xLabel, xLabel, yLabel, dataset,
        PlotOrientation.VERTICAL, true, true, false);

    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.WHITE);
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

    // Style the bars
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setSeriesPaint(0, COLORS[0]);

    return chart;
  }

  // Additional helper methods

  private static void styleChart(JFreeChart chart) {
    // Apply styling based on current style
    if ("ggplot".equals(currentStyle)) {
      applyGgplotStyle(chart);
    } else if ("dark".equals(currentStyle)) {
      applyDarkStyle(chart);
    }
    // Default style is already applied in the creation methods
  }

  private static void applyGgplotStyle(JFreeChart chart) {
    // Apply ggplot-like styling
    chart.setBackgroundPaint(new Color(0xF0F0F0));

    if (chart.getPlot() instanceof XYPlot plot) {
      plot.setBackgroundPaint(new Color(0xE8E8E8));
      plot.setDomainGridlinePaint(Color.WHITE);
      plot.setRangeGridlinePaint(Color.WHITE);
      plot.setDomainGridlineStroke(new BasicStroke(1.0f));
      plot.setRangeGridlineStroke(new BasicStroke(1.0f));
    } else if (chart.getPlot() instanceof CategoryPlot plot) {
      plot.setBackgroundPaint(new Color(0xE8E8E8));
      plot.setDomainGridlinePaint(Color.WHITE);
      plot.setRangeGridlinePaint(Color.WHITE);
      plot.setDomainGridlineStroke(new BasicStroke(1.0f));
      plot.setRangeGridlineStroke(new BasicStroke(1.0f));
    }
  }

  private static void applyDarkStyle(JFreeChart chart) {
    // Apply dark theme styling
    chart.setBackgroundPaint(new Color(0x2E2E2E));
    chart.getTitle().setPaint(Color.WHITE);

    if (chart.getLegend() != null) {
      chart.getLegend().setBackgroundPaint(new Color(0x2E2E2E));
      chart.getLegend().setItemPaint(Color.WHITE);
    }

    if (chart.getPlot() instanceof XYPlot plot) {
      plot.setBackgroundPaint(new Color(0x3A3A3A));
      plot.setDomainGridlinePaint(new Color(0x666666));
      plot.setRangeGridlinePaint(new Color(0x666666));
      plot.getDomainAxis().setLabelPaint(Color.WHITE);
      plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
      plot.getRangeAxis().setLabelPaint(Color.WHITE);
      plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
    } else if (chart.getPlot() instanceof CategoryPlot plot) {
      plot.setBackgroundPaint(new Color(0x3A3A3A));
      plot.setDomainGridlinePaint(new Color(0x666666));
      plot.setRangeGridlinePaint(new Color(0x666666));
      plot.getDomainAxis().setLabelPaint(Color.WHITE);
      plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
      plot.getRangeAxis().setLabelPaint(Color.WHITE);
      plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
    }
  }

  private static boolean isNumeric(Series<?> series) {
    if (series.size() == 0) {
      return false;
    }

    // Check the first non-null value
    for (int i = 0; i < series.size(); i++) {
      Object value = series.get(i);
      if (value != null) {
        try {
          Double.parseDouble(value.toString());
          return true;
        } catch (NumberFormatException e) {
          return false;
        }
      }
    }

    return false;
  }

  private static void adjustAxisLabels(JFreeChart chart, boolean showXLabel, boolean showYLabel, String xAxisLabel,
      String yAxisLabel) {
    XYPlot plot = (XYPlot) chart.getPlot();

    // X axis label
    NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
    if (showXLabel) {
      xAxis.setLabel(xAxisLabel);
      xAxis.setTickLabelsVisible(true);
    } else {
      xAxis.setLabel(null);
      xAxis.setTickLabelsVisible(false);
    }

    // Y axis label
    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
    if (showYLabel) {
      yAxis.setLabel(yAxisLabel);
      yAxis.setTickLabelsVisible(true);
    } else {
      yAxis.setLabel(null);
      yAxis.setTickLabelsVisible(false);
    }
  }
}