package org.jjavaglue.math;

import org.dflib.*;

/**
 * Utility class for converting between DFLib and Commons Math data structures.
 */
public class DataConverter {

  /**
   * Converts a DFLib DataFrame to a 2D double array.
   * Handles numeric columns only. Categorical columns should be one-hot encoded first.
   *
   * @param df the input DataFrame
   * @return a 2D array where each row corresponds to a row in the DataFrame
   */
  public static double[][] dataFrameToArray(DataFrame df) {
    int rows = df.height();
    int cols = df.width();
    double[][] result = new double[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Object value = df.get(j, i);
        result[i][j] = convertToDouble(value);
      }
    }

    return result;
  }

  /**
   * Converts a DFLib Series to a double array.
   *
   * @param series the input Series
   * @return a double array containing the numeric values of the Series
   */
  public static double[] seriesToDoubleArray(Series<?> series) {
    // Optimize for common series types
    if (series instanceof DoubleSeries) {
      return ((DoubleSeries) series).toDoubleArray();
    } else if (series instanceof IntSeries) {
      int[] intArray = ((IntSeries) series).toIntArray();
      double[] result = new double[intArray.length];
      for (int i = 0; i < intArray.length; i++) {
        result[i] = intArray[i];
      }
      return result;
    } else if (series instanceof LongSeries) {
      long[] longArray = ((LongSeries) series).toLongArray();
      double[] result = new double[longArray.length];
      for (int i = 0; i < longArray.length; i++) {
        result[i] = longArray[i];
      }
      return result;
    } else {
      // Generic path for other series types
      int size = series.size();
      double[] result = new double[size];

      for (int i = 0; i < size; i++) {
        result[i] = convertToDouble(series.get(i));
      }

      return result;
    }
  }

  /**
   * Converts a DFLib Series to an int array.
   *
   * @param series the input Series
   * @return an int array containing the integer values of the Series
   */
  public static int[] seriesToIntArray(Series<?> series) {
    // Optimize for common series types
    if (series instanceof IntSeries) {
      return ((IntSeries) series).toIntArray();
    } else {
      int size = series.size();
      int[] result = new int[size];

      for (int i = 0; i < size; i++) {
        Object value = series.get(i);
        if (value == null) {
          result[i] = 0; // Default for null values
        } else if (value instanceof Number) {
          result[i] = ((Number) value).intValue();
        } else if (value instanceof String) {
          try {
            result[i] = Integer.parseInt((String) value);
          } catch (NumberFormatException e) {
            result[i] = 0; // Default for non-numeric strings
          }
        } else {
          result[i] = 0; // Default for other types
        }
      }

      return result;
    }
  }

  /**
   * Converts various types to double.
   *
   * @param value the input value
   * @return the value converted to double
   */
  private static double convertToDouble(Object value) {
    if (value == null) {
      return Double.NaN;
    } else if (value instanceof Number) {
      return ((Number) value).doubleValue();
    } else if (value instanceof String) {
      try {
        return Double.parseDouble((String) value);
      } catch (NumberFormatException e) {
        // Handle categorical values - could use one-hot encoding
        // For now, just return NaN for non-numeric strings
        return Double.NaN;
      }
    } else {
      // For other types, just return NaN
      return Double.NaN;
    }
  }
}