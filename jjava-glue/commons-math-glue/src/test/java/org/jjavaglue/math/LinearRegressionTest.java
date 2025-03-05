package org.jjavaglue.math;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.series.ArraySeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the LinearRegression class.
 */
public class LinearRegressionTest {

  private DataFrame testData;
  private Series<?> testTarget;
  private LinearRegression model;

  @BeforeEach
  public void setUp() {
    // Create a simple synthetic dataset for testing
    // X values from 1 to 10
    Series<Double> xSeries = new ArraySeries<>(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

    // y = 2x + 5 + some noise
    Series<Double> ySeries = new ArraySeries<>(7.1, 9.0, 11.2, 13.3, 15.0, 16.8, 19.1, 21.2, 23.0, 25.5);

    // Create DataFrame with x feature
    testData = DataFrame.byColumn("x").of(xSeries);

    // Store target Series
    testTarget = ySeries;

    // Create the model
    model = new LinearRegression();
  }

  @Test
  @DisplayName("LinearRegression should fit a simple line correctly")
  public void testLinearRegressionFit() {
    // Fit the model
    model.fit(testData, testTarget);

    // The model summary should contain meaningful information
    String summary = model.summary();
    assertThat(summary).contains("Linear Regression Model");
    assertThat(summary).contains("Formula: y =");

    // For a simple line y = 2x + 5, we expect the intercept to be close to 5
    // and the coefficient of x to be close to 2
    assertThat(model.getIntercept()).isCloseTo(5.0, Offset.offset(0.5));
    assertThat(model.getCoefficients()[0]).isCloseTo(2.0, Offset.offset(0.1));

    // The R² should be high for this synthetic data
    assertThat(model.getRSquared()).isGreaterThan(0.99);
  }

  @Test
  @DisplayName("LinearRegression should predict values accurately")
  public void testLinearRegressionPredict() {
    // Fit the model
    model.fit(testData, testTarget);

    // Create new test data for prediction
    Series<Double> newXSeries = new ArraySeries<>(2.5, 5.5, 7.5);
    DataFrame newData = DataFrame.byColumn("x").of(newXSeries);

    // Make predictions
    double[] predictions = model.predict(newData);

    // Expected values based on y = 2x + 5
    double[] expected = { 10.0, 16.0, 20.0 };

    // Check predictions are close to expected values (allowing for some noise)
    assertThat(predictions).hasSize(3);
    for (int i = 0; i < predictions.length; i++) {
      assertThat(predictions[i]).isCloseTo(expected[i], Offset.offset(1.0));
    }
  }

  @Test
  @DisplayName("LinearRegression model should throw exception when predicting without fitting")
  public void testPredictWithoutFitting() {
    // Create new test data for prediction
    Series<Double> newXSeries = new ArraySeries<>(2.5, 5.5, 7.5);
    DataFrame newData = DataFrame.byColumn("x").of(newXSeries);

    // Should throw exception when trying to predict with unfitted model
    Assertions.assertThatThrownBy(() -> model.predict(newData)).isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("must be trained");
  }

  @Test
  @DisplayName("LinearRegression should handle multiple predictors")
  public void testMultiplePredictors() {
    // Create a more complex dataset with multiple predictors
    Series<Double> x1Series = new ArraySeries<>(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
    Series<Double> x2Series = new ArraySeries<>(5.0, 4.0, 6.0, 3.0, 7.0, 2.0, 8.0, 1.0, 9.0, 0.0);

    // y = 2*x1 + 1*x2 + 3 + noise
    Series<Double> multiYSeries = new ArraySeries<>(10.1, 10.2, 15.3, 14.9, 20.0, 17.1, 25.0, 19.8, 30.2, 22.9);

    // Create DataFrame with multiple features
    DataFrame multiX = DataFrame.byColumn("x1", "x2").of(x1Series, x2Series);

    // Fit the model
    model.fit(multiX, multiYSeries);

    // Make predictions on training data
    double[] predictions = model.predict(multiX);

    // Check predictions match reasonably well with training data
    assertThat(predictions).hasSize(10);
    double mse = 0;
    for (int i = 0; i < predictions.length; i++) {
      double actual = ((Number) multiYSeries.get(i)).doubleValue();
      mse += Math.pow(actual - predictions[i], 2);
    }
    mse /= predictions.length;

    // MSE should be low for a good fit
    assertThat(mse).isLessThan(1.0);

    // Model summary should contain both x1 and x2
    String summary = model.summary();
    assertThat(summary).contains("x1");
    assertThat(summary).contains("x2");
  }
}