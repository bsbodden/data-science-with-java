package org.jjavaglue.core;

import java.util.List;

public class Convert {
  public static double[] floatArrayToDoubleArray(float[] floatArray) {
    // Initialize double array of the same size as the float array
    double[] doubleArray = new double[floatArray.length];

    // Iterate over the float array, convert each float to double, and store it in the double array
    for (int i = 0; i < floatArray.length; i++) {
      doubleArray[i] = floatArray[i];
    }

    return doubleArray;
  }

  public static float[] doubleArrayToFloatArray(double[] doubleArray) {
    // Initialize float array of the same size as the List<Double>
    float[] floatArray = new float[doubleArray.length];

    // Iterate over the List<Double>, convert each Double to float, and store it in the float array
    for (int i = 0; i < doubleArray.length; i++) {
      floatArray[i] = Double.valueOf(doubleArray[i]).floatValue();
    }

    return floatArray;
  }
}
