package org.jjavaglue.math;

import org.dflib.DataFrame;
import org.dflib.Series;

/**
 * Common interface for all machine learning models.
 */
public interface Model {

  /**
   * Trains the model on the given training data.
   *
   * @param X the training features
   * @param y the training targets
   * @return the trained model (for method chaining)
   */
  Model fit(DataFrame X, Series<?> y);

  /**
   * Makes predictions using the trained model.
   *
   * @param X the input features to predict
   * @return an array of predictions
   */
  double[] predict(DataFrame X);

  /**
   * Returns a string representation of the model.
   *
   * @return a string describing the model and its parameters
   */
  String summary();
}