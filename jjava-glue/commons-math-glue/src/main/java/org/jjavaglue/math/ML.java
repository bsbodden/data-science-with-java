package org.jjavaglue.math;

import org.dflib.DataFrame;
import org.dflib.Series;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main entry point for machine learning operations.
 * Provides utility methods for data preparation, model creation, and evaluation.
 */
public class ML {

  /**
   * Creates a linear regression model.
   *
   * @return a new LinearRegression model instance
   */
  public static LinearRegression linearRegression() {
    return new LinearRegression();
  }

  /**
   * Splits a DataFrame into features (X) and target (y).
   *
   * @param df           the input DataFrame
   * @param targetColumn the name of the target column
   * @return an array containing [X, y] where X is a DataFrame of features and y is a Series of targets
   */
  public static Object[] splitTarget(DataFrame df, String targetColumn) {
    Series<?> y = df.getColumn(targetColumn);

    // DFLib doesn't have a dropColumns method, use cols().exclude instead
    DataFrame X = df.colsExcept(targetColumn).select();

    return new Object[] { X, y };
  }

  /**
   * Splits the data into training and test sets.
   *
   * @param X          the feature DataFrame
   * @param y          the target Series
   * @param testSize   the proportion of the dataset to include in the test split (0-1)
   * @param randomSeed the random seed for reproducibility
   * @return a TrainTestSplit object containing the split datasets
   */
  public static TrainTestSplit trainTestSplit(DataFrame X, Series<?> y, double testSize, long randomSeed) {
    int n = X.height();
    int testCount = (int) Math.round(testSize * n);
    int trainCount = n - testCount;

    // Generate random indices
    List<Integer> indices = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      indices.add(i);
    }

    // Shuffle indices
    Random random = new Random(randomSeed);
    for (int i = n - 1; i > 0; i--) {
      int j = random.nextInt(i + 1);
      int temp = indices.get(i);
      indices.set(i, indices.get(j));
      indices.set(j, temp);
    }

    // Get train indices
    List<Integer> trainIndices = indices.subList(0, trainCount);

    // Get test indices
    List<Integer> testIndices = indices.subList(trainCount, n);

    // Convert indices to int arrays
    int[] trainIndexArray = trainIndices.stream().mapToInt(i -> i).toArray();
    int[] testIndexArray = testIndices.stream().mapToInt(i -> i).toArray();

    // Create train and test DataFrames - we need to call select() on RowSet
    DataFrame XTrain = X.rows(trainIndexArray).select();
    DataFrame XTest = X.rows(testIndexArray).select();

    // Create train and test Series
    Series<?> yTrain = y.select(trainIndexArray);
    Series<?> yTest = y.select(testIndexArray);

    return new TrainTestSplit(XTrain, yTrain, XTest, yTest);
  }

  /**
   * Class to hold the result of a train-test split.
   */
  public static class TrainTestSplit {
    public final DataFrame xTrain;
    public final Series<?> yTrain;
    public final DataFrame xTest;
    public final Series<?> yTest;

    public TrainTestSplit(DataFrame xTrain, Series<?> yTrain, DataFrame xTest, Series<?> yTest) {
      this.xTrain = xTrain;
      this.yTrain = yTrain;
      this.xTest = xTest;
      this.yTest = yTest;
    }
  }
}