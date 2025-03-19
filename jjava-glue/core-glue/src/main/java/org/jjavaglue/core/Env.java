package org.jjavaglue.core;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Environment utilities for Java Jupyter notebooks.
 * Provides constants and utilities for working with the Jupyter environment.
 */
public class Env {
  /**
   * Base directory for notebooks in the Jupyter container
   */
  public static final String NOTEBOOKS_DIR = "/home/jovyan/notebooks/";

  /**
   * Data directory within notebooks
   */
  public static final String DATA_DIR = NOTEBOOKS_DIR + "data/";

  /**
   * Helper method to get a path relative to the notebooks directory
   *
   * @param relativePath path relative to the notebooks directory
   * @return full path
   */
  public static String getNotebooksPath(String relativePath) {
    return NOTEBOOKS_DIR + relativePath;
  }

  /**
   * Helper method to get a path relative to the data directory
   *
   * @param filename name of the file in the data directory
   * @return full path to the file
   */
  public static String getDataPath(String filename) {
    return DATA_DIR + filename;
  }

  /**
   * Print a message to the console (shorthand for System.out.println)
   *
   * @param message the message to print
   */
  public static void println(Object message) {
    System.out.println(message);
  }

  /**
   * Print a formatted message to the console (shorthand for System.out.printf)
   *
   * @param format the format string
   * @param args   the arguments to format
   */
  public static void printf(String format, Object... args) {
    System.out.printf(format, args);
  }

  private static final Map<String, String> sessionEnv = new HashMap<>();

  public static void promptAndSetEnv(String key) {
    String value = readPassword(key + ": ");
    sessionEnv.put(key, value);
  }

  public static String getEnv(String key) {
    return sessionEnv.get(key);
  }

  private static String readPassword(String prompt) {
    // Try to use Console first for password masking
    Console console = System.console();
    if (console != null) {
      return new String(console.readPassword(prompt));
    } else {
      // Fall back to Scanner for environments without Console (like Jupyter)
      System.out.print(prompt);
      Scanner scanner = new Scanner(System.in);
      return scanner.nextLine();
    }
  }
}
