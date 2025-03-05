package org.jjavaglue.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class JTest {
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
  void testConstants() {
    assertThat(J.NOTEBOOKS_DIR).isEqualTo(Env.NOTEBOOKS_DIR);
    assertThat(J.DATA_DIR).isEqualTo(Env.DATA_DIR);
  }

  @Test
  void testGetNotebooksPath() {
    assertThat(J.getNotebooksPath("test.ipynb")).isEqualTo(Env.getNotebooksPath("test.ipynb"));
  }

  @Test
  void testGetDataPath() {
    assertThat(J.getDataPath("test.csv")).isEqualTo(Env.getDataPath("test.csv"));
  }

  @Test
  void testPrintln() {
    J.println("Hello, J!");
    assertThat(outContent.toString()).isEqualTo("Hello, J!\n");
  }

  @Test
  void testPrintf() {
    J.printf("Test: %s", "success");
    assertThat(outContent.toString()).isEqualTo("Test: success");
  }
}
