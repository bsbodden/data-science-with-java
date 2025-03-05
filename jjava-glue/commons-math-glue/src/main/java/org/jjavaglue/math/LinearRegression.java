package org.jjavaglue.math;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.dflib.DataFrame;
import org.dflib.Series;

/**
 * Linear Regression model implementation using Apache Commons Math.
 */
public class LinearRegression implements Model {

  private OLSMultipleLinearRegression regression;
  private double[] coefficients;
  private double intercept;
  private String[] featureNames;
  private double rSquared;
  private double adjustedRSquared;
  private double meanSquareError;

  /**
   * Creates a new Linear Regression model.
   */
  public LinearRegression() {
  }

  @Override
  public Model fit(DataFrame X, Series<?> y) {
    // Convert DFLib DataFrame to double[][]
    double[][] xData = DataConverter.dataFrameToArray(X);
    double[] yData = DataConverter.seriesToDoubleArray(y);

    // Store feature names
    featureNames = X.getColumnsIndex().toArray();

    // Create and train Commons Math regression model
    regression = new OLSMultipleLinearRegression();
    regression.newSampleData(yData, xData);

    // Extract model parameters
    double[] fullCoefficients = regression.estimateRegressionParameters();
    intercept = fullCoefficients[0];
    coefficients = new double[fullCoefficients.length - 1];
    System.arraycopy(fullCoefficients, 1, coefficients, 0, coefficients.length);

    // Store regression statistics
    rSquared = regression.calculateRSquared();
    adjustedRSquared = regression.calculateAdjustedRSquared();
    meanSquareError = regression.estimateErrorVariance();

    return this;
  }

  @Override
  public double[] predict(DataFrame X) {
    if (regression == null) {
      throw new IllegalStateException("Model must be trained with fit() before making predictions");
    }

    double[][] xData = DataConverter.dataFrameToArray(X);
    double[] predictions = new double[xData.length];

    for (int i = 0; i < xData.length; i++) {
      predictions[i] = intercept;
      for (int j = 0; j < xData[i].length && j < coefficients.length; j++) {
        predictions[i] += coefficients[j] * xData[i][j];
      }
    }

    return predictions;
  }

  @Override
  public String summary() {
    if (regression == null) {
      return "Untrained Linear Regression Model";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Linear Regression Model\n");
    sb.append("----------------------\n");
    sb.append("Formula: y = ");

    // Intercept
    sb.append(String.format("%.4f", intercept));

    // Coefficients with feature names
    for (int i = 0; i < coefficients.length; i++) {
      double coef = coefficients[i];
      String featureName = i < featureNames.length ? featureNames[i] : "x" + i;

      if (coef >= 0) {
        sb.append(" + ");
      } else {
        sb.append(" - ");
        coef = -coef;
      }

      sb.append(String.format("%.4f * %s", coef, featureName));
    }

    // Model statistics
    sb.append("\n\nR²: ").append(String.format("%.4f", rSquared));
    sb.append("\nAdjusted R²: ").append(String.format("%.4f", adjustedRSquared));
    sb.append("\nResidual Standard Error: ").append(String.format("%.4f", Math.sqrt(meanSquareError)));

    return sb.toString();
  }

  /**
   * Gets the feature names used in the model.
   *
   * @return array of feature names
   */
  public String[] getFeatureNames() {
    return featureNames;
  }

  /**
   * Gets the coefficients of the model.
   *
   * @return array of coefficients
   */
  public double[] getCoefficients() {
    return coefficients;
  }

  /**
   * Gets the intercept of the model.
   *
   * @return intercept value
   */
  public double getIntercept() {
    return intercept;
  }

  /**
   * Gets the R-squared value of the model.
   *
   * @return R-squared value
   */
  public double getRSquared() {
    return rSquared;
  }

  /**
   * Gets the adjusted R-squared value of the model.
   *
   * @return adjusted R-squared value
   */
  public double getAdjustedRSquared() {
    return adjustedRSquared;
  }

  /**
   * Gets the mean square error of the model.
   *
   * @return mean square error
   */
  public double getMeanSquareError() {
    return meanSquareError;
  }
}