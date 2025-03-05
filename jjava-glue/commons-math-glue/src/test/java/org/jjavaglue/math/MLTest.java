package org.jjavaglue.math;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.series.ArraySeries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the ML utility class.
 */
public class MLTest {

  @Test
  @DisplayName("trainTestSplit should split data correctly")
  public void testTrainTestSplit() {
    // Create test data
    Series<Double> x1 = new ArraySeries<>(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
    Series<Double> x2 = new ArraySeries<>(10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0);
    Series<Double> y = new ArraySeries<>(11.0, 11.0, 11.0, 11.0, 11.0, 11.0, 11.0, 11.0, 11.0, 11.0);

    DataFrame X = DataFrame.byColumn("x1", "x2").of(x1, x2);

    // Split with test_size=0.3
    ML.TrainTestSplit split = ML.trainTestSplit(X, y, 0.3, 42);

    // Check sizes
    assertThat(split.xTrain.height()).isEqualTo(7);
    assertThat(split.xTest.height()).isEqualTo(3);
    assertThat(split.yTrain.size()).isEqualTo(7);
    assertThat(split.yTest.size()).isEqualTo(3);

    // Check that all features are preserved
    assertThat(split.xTrain.getColumnsIndex()).contains("x1", "x2");
    assertThat(split.xTest.getColumnsIndex()).contains("x1", "x2");
  }

  @Test
  @DisplayName("splitTarget should separate features and target correctly")
  public void testSplitTarget() {
    // Create test data
    Series<Double> x1 = new ArraySeries<>(1.0, 2.0, 3.0);
    Series<Double> x2 = new ArraySeries<>(4.0, 5.0, 6.0);
    Series<Double> y = new ArraySeries<>(7.0, 8.0, 9.0);

    DataFrame df = DataFrame.byColumn("x1", "x2", "y").of(x1, x2, y);

    // Split target
    Object[] split = ML.splitTarget(df, "y");
    DataFrame X = (DataFrame) split[0];
    Series<?> target = (Series<?>) split[1];

    // Check structure
    assertThat(X.width()).isEqualTo(2);
    assertThat(X.height()).isEqualTo(3);
    assertThat(X.getColumnsIndex()).contains("x1", "x2");
    assertThat(X.getColumnsIndex()).doesNotContain("y");

    // Check target
    assertThat(target.size()).isEqualTo(3);
    assertThat(target.get(0)).isEqualTo(7.0);
    assertThat(target.get(1)).isEqualTo(8.0);
    assertThat(target.get(2)).isEqualTo(9.0);
  }

  @Test
  @DisplayName("linearRegression should create a working model instance")
  public void testLinearRegressionFactory() {
    // Create model using factory method
    LinearRegression model = ML.linearRegression();

    // Verify we got a valid instance
    assertThat(model).isNotNull();
    assertThat(model).isInstanceOf(LinearRegression.class);
  }
}