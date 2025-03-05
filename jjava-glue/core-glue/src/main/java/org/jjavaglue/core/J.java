package org.jjavaglue.core;

/**
 * Convenience class with static imports to simplify notebook code.
 * Use this class with a static import to access all methods directly:
 * import static org.jjavaglue.core.J.*;
 */
public class J {
  /**
   * Base directory for notebooks in the Jupyter container
   */
  public static final String NOTEBOOKS_DIR = Env.NOTEBOOKS_DIR;

  /**
   * Data directory within notebooks
   */
  public static final String DATA_DIR = Env.DATA_DIR;

  /**
   * Helper method to get a path relative to the notebooks directory
   *
   * @param relativePath path relative to the notebooks directory
   * @return full path
   */
  public static String getNotebooksPath(String relativePath) {
    return Env.getNotebooksPath(relativePath);
  }

  /**
   * Helper method to get a path relative to the data directory
   *
   * @param filename name of the file in the data directory
   * @return full path to the file
   */
  public static String getDataPath(String filename) {
    return Env.getDataPath(filename);
  }

  /**
   * Print a message to the console (shorthand for System.out.println)
   *
   * @param message the message to print
   */
  public static void println(Object message) {
    Env.println(message);
  }

  /**
   * Print a formatted message to the console (shorthand for System.out.printf)
   *
   * @param format the format string
   * @param args   the arguments to format
   */
  public static void printf(String format, Object... args) {
    Env.printf(format, args);
  }
}