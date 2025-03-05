package org.jjavaglue.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class EnvTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void setUpStreams() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void restoreStreams() {
    System.setOut(originalOut);
  }

  @Test
  void testNotebooksDir() {
    assertThat(Env.NOTEBOOKS_DIR).isEqualTo("/home/jovyan/notebooks/");
  }

  @Test
  void testDataDir() {
    assertThat(Env.DATA_DIR).isEqualTo("/home/jovyan/notebooks/data/");
  }

  @Test
  void testGetNotebooksPath() {
    assertThat(Env.getNotebooksPath("test.ipynb")).isEqualTo("/home/jovyan/notebooks/test.ipynb");
    assertThat(Env.getNotebooksPath("subfolder/test.ipynb")).isEqualTo("/home/jovyan/notebooks/subfolder/test.ipynb");
  }

  @Test
  void testGetDataPath() {
    assertThat(Env.getDataPath("test.csv")).isEqualTo("/home/jovyan/notebooks/data/test.csv");
  }

  @Test
  void testPrintln() {
    Env.println("Hello, World!");
    assertThat(outContent.toString()).isEqualTo("Hello, World!\n");
  }

  @Test
  void testPrintlnWithObject() {
    Env.println(123);
    assertThat(outContent.toString()).isEqualTo("123\n");
  }

  @Test
  void testPrintf() {
    Env.printf("Number: %d, String: %s", 42, "test");
    assertThat(outContent.toString()).isEqualTo("Number: 42, String: test");
  }
}