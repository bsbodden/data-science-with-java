package org.jjavaglue.math;

import org.dflib.Series;

/**
 * Utility class for calculating model evaluation metrics.
 */
public class Metrics {

  /**
   * Calculates the Mean Absolute Error.
   *
   * @param truth       the true values
   * @param predictions the predicted values
   * @return the mean absolute error
   */
  public static double mae(Series<?> truth, double[] predictions) {
    double[] truthArray = DataConverter.seriesToDoubleArray(truth);
    double sum = 0.0;

    for (int i = 0; i < truthArray.length; i++) {
      sum += Math.abs(truthArray[i] - predictions[i]);
    }

    return sum / truthArray.length;
  }

  /**
   * Calculates the Mean Squared Error.
   *
   * @param truth       the true values
   * @param predictions the predicted values
   * @return the mean squared error
   */
  public static double mse(Series<?> truth, double[] predictions) {
    double[] truthArray = DataConverter.seriesToDoubleArray(truth);
    double sum = 0.0;

    for (int i = 0; i < truthArray.length; i++) {
      double diff = truthArray[i] - predictions[i];
      sum += diff * diff;
    }

    return sum / truthArray.length;
  }

  /**
   * Calculates the Root Mean Squared Error.
   *
   * @param truth       the true values
   * @param predictions the predicted values
   * @return the root mean squared error
   */
  public static double rmse(Series<?> truth, double[] predictions) {
    return Math.sqrt(mse(truth, predictions));
  }

  /**
   * Calculates the R-squared (coefficient of determination).
   *
   * @param truth       the true values
   * @param predictions the predicted values
   * @return the R-squared value
   */
  public static double r2(Series<?> truth, double[] predictions) {
    double[] truthArray = DataConverter.seriesToDoubleArray(truth);
    double meanTruth = 0.0;

    // Calculate mean of true values
    for (double value : truthArray) {
      meanTruth += value;
    }
    meanTruth /= truthArray.length;

    // Calculate total sum of squares and residual sum of squares
    double ssTot = 0.0;
    double ssRes = 0.0;

    for (int i = 0; i < truthArray.length; i++) {
      ssTot += Math.pow(truthArray[i] - meanTruth, 2);
      ssRes += Math.pow(truthArray[i] - predictions[i], 2);
    }

    // R² = 1 - (SSres / SStot)
    return 1 - (ssRes / ssTot);
  }
}