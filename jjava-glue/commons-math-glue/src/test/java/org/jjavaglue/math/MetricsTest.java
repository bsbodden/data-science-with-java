package org.jjavaglue.math;

import org.assertj.core.data.Offset;
import org.dflib.Series;
import org.dflib.series.ArraySeries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Metrics class.
 */
public class MetricsTest {

  @Test
  @DisplayName("MAE calculation should match expected value")
  public void testMAE() {
    // Create test data
    Series<Double> truth = new ArraySeries<>(10.0, 20.0, 30.0, 40.0, 50.0);
    double[] predictions = { 12.0, 18.0, 28.0, 45.0, 55.0 };

    // Calculate MAE
    double mae = Metrics.mae(truth, predictions);

    // Expected: (|10-12| + |20-18| + |30-28| + |40-45| + |50-55|) / 5 = (2 + 2 + 2 + 5 + 5) / 5 = 16/5 = 3.2
    assertThat(mae).isCloseTo(3.2, Offset.offset(0.0001));
  }

  @Test
  @DisplayName("MSE calculation should match expected value")
  public void testMSE() {
    // Create test data
    Series<Double> truth = new ArraySeries<>(10.0, 20.0, 30.0, 40.0, 50.0);
    double[] predictions = { 12.0, 18.0, 28.0, 45.0, 55.0 };

    // Calculate MSE
    double mse = Metrics.mse(truth, predictions);

    // Expected: ((10-12)² + (20-18)² + (30-28)² + (40-45)² + (50-55)²) / 5 = (4 + 4 + 4 + 25 + 25) / 5 = 62/5 = 12.4
    assertThat(mse).isCloseTo(12.4, Offset.offset(0.0001));
  }

  @Test
  @DisplayName("RMSE calculation should match expected value")
  public void testRMSE() {
    // Create test data
    Series<Double> truth = new ArraySeries<>(10.0, 20.0, 30.0, 40.0, 50.0);
    double[] predictions = { 12.0, 18.0, 28.0, 45.0, 55.0 };

    // Calculate RMSE
    double rmse = Metrics.rmse(truth, predictions);

    // Expected: sqrt(MSE) = sqrt(12.4) ≈ 3.5214
    assertThat(rmse).isCloseTo(3.5214, Offset.offset(0.0001));
  }

  @Test
  @DisplayName("R² calculation should match expected value")
  public void testR2() {
    // Create test data with a clear linear relationship plus some noise
    Series<Double> truth = new ArraySeries<>(10.0, 20.0, 30.0, 40.0, 50.0);
    double[] predictions = { 12.0, 18.0, 32.0, 38.0, 50.0 };

    // Calculate R²
    double r2 = Metrics.r2(truth, predictions);

    // The predictions follow the trend closely, so R² should be high
    assertThat(r2).isGreaterThan(0.9);
  }

  @Test
  @DisplayName("R² should be 1.0 for perfect predictions")
  public void testR2Perfect() {
    // Create test data with perfect predictions
    Series<Double> truth = new ArraySeries<>(10.0, 20.0, 30.0, 40.0, 50.0);
    double[] predictions = { 10.0, 20.0, 30.0, 40.0, 50.0 };

    // Calculate R²
    double r2 = Metrics.r2(truth, predictions);

    // Perfect predictions should give R² = 1.0
    assertThat(r2).isEqualTo(1.0);
  }

  @Test
  @DisplayName("R² should be close to 0 for predictions that just use the mean")
  public void testR2Mean() {
    // Create test data
    Series<Double> truth = new ArraySeries<>(10.0, 20.0, 30.0, 40.0, 50.0);
    // Predictions are just the mean value of truth (30.0)
    double[] predictions = { 30.0, 30.0, 30.0, 30.0, 30.0 };

    // Calculate R²
    double r2 = Metrics.r2(truth, predictions);

    // Using the mean as prediction should give R² = 0
    assertThat(r2).isCloseTo(0.0, Offset.offset(0.0001));
  }

  @Test
  @DisplayName("R² can be negative for bad models")
  public void testR2Negative() {
    // Create test data
    Series<Double> truth = new ArraySeries<>(10.0, 20.0, 30.0, 40.0, 50.0);
    // Predictions are opposite of the pattern
    double[] predictions = { 50.0, 40.0, 30.0, 20.0, 10.0 };

    // Calculate R²
    double r2 = Metrics.r2(truth, predictions);

    // Using predictions that are worse than just using the mean gives negative R²
    assertThat(r2).isLessThan(0.0);
  }
}