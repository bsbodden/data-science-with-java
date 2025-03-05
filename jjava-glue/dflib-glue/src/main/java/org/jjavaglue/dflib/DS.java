package org.jjavaglue.dflib;

import org.dflib.*;
import org.dflib.csv.Csv;
import org.dflib.print.TabularPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simplified API for DFLib that provides pandas-like convenience methods
 */
public class DS {
  /**
   * Load a CSV file with smart defaults
   */
  public static DataFrame read(String path) {
    return Csv.load(path);
  }

  /**
   * Create a DataFrame from lists (column-oriented)
   * Each list becomes a column in the DataFrame
   */
  public static DataFrame create(String[] columns, List<?>... data) {
    // First, verify that all lists have the same length
    int rowCount = data[0].size();
    for (List<?> col : data) {
      if (col.size() != rowCount) {
        throw new IllegalArgumentException("All lists must have the same length");
      }
    }

    // Create row data
    List<Map<String, Object>> rows = new ArrayList<>();

    // For each row index
    for (int i = 0; i < rowCount; i++) {
      // Create a new row
      Map<String, Object> row = new HashMap<>();

      // Add values from each column
      for (int j = 0; j < columns.length; j++) {
        row.put(columns[j], data[j].get(i));
      }

      rows.add(row);
    }

    // Create DataFrame from rows
    DataFrame df = DataFrame.empty(columns);
    for (Map<String, Object> row : rows) {
      df = df.addRow(row);
    }

    return df;
  }

  /**
   * Create a DataFrame from a Map of column names to values
   */
  public static DataFrame fromMap(Map<String, List<?>> data) {
    String[] columns = data.keySet().toArray(new String[0]);
    List<?>[] columnData = new List[columns.length];

    for (int i = 0; i < columns.length; i++) {
      columnData[i] = data.get(columns[i]);
    }

    return create(columns, columnData);
  }

  /**
   * Print data with improved formatting
   */
  public static void show(DataFrame df) {
    show(df, 10);
  }

  /**
   * Print data with improved formatting and custom number of rows
   */
  public static void show(DataFrame df, int rows) {
    TabularPrinter printer = new TabularPrinter();
    StringBuilder sb = new StringBuilder();
    printer.print(sb, df.head(rows));
    System.out.println(sb);

    // Print remaining rows count if truncated
    if (df.height() > rows) {
      System.out.println("... " + (df.height() - rows) + " more rows");
    }
  }

  /**
   * Provide a summary of the DataFrame structure and contents
   */
  public static void describe(DataFrame df) {
    System.out.println("DataFrame: " + df.width() + " columns × " + df.height() + " rows");
    System.out.println("\nColumns:");

    for (String col : df.getColumnsIndex()) {
      Series<?> series = df.getColumn(col);
      String typeInfo = series.getClass().getSimpleName();

      // Get sample values
      List<String> samples = new ArrayList<>();
      for (int i = 0; i < Math.min(3, series.size()); i++) {
        Object value = series.get(i);
        samples.add(value == null ? "null" : value.toString());
      }

      System.out.printf("  %-20s %-20s Sample: [%s]\n", col, typeInfo, String.join(", ", samples));
    }

    // Show first 5 rows
    System.out.println("\nFirst 5 rows:");
    show(df.head(5));

    // Show numeric summaries for numeric columns
    System.out.println("\nNumeric Summaries:");
    for (String col : df.getColumnsIndex()) {
      Series<?> series = df.getColumn(col);

      // Check if this series can be converted to double
      try {
        if (series instanceof DoubleSeries ds) {
          System.out.printf("  %-20s min: %10.2f  max: %10.2f  mean: %10.2f  sum: %10.2f\n", col, ds.min(), ds.max(),
              ds.avg(), ds.sum());
        } else if (series instanceof IntSeries is) {
          System.out.printf("  %-20s min: %10d  max: %10d  mean: %10.2f  sum: %10d\n", col, is.min(), is.max(),
              is.avg(), is.sum());
        } else if (series instanceof LongSeries ls) {
          System.out.printf("  %-20s min: %10d  max: %10d  mean: %10.2f  sum: %10d\n", col, ls.min(), ls.max(),
              ls.avg(), ls.sum());
        }
      } catch (Exception e) {
        // Skip non-numeric columns or ones that can't be summarized
      }
    }
  }

  /**
   * Sort DataFrame by a column
   */
  public static DataFrame sort(DataFrame df, String column) {
    return df.sort(column, true);
  }

  /**
   * Sort DataFrame by a column with direction
   */
  public static DataFrame sort(DataFrame df, String column, boolean ascending) {
    return df.sort(column, ascending);
  }

  /**
   * Save DataFrame to CSV
   */
  public static void toCsv(DataFrame df, String path) throws IOException {
    Csv.save(df, path);
  }
}