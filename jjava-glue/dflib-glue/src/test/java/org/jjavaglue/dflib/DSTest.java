package org.jjavaglue.dflib;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DSTest {

  private DataFrame sampleDataFrame;

  @BeforeEach
  void setup() {
    // Create a sample DataFrame for testing
    Map<String, List<?>> data = new HashMap<>();
    data.put("id", Arrays.asList(1, 2, 3, 4, 5));
    data.put("name", Arrays.asList("Java", "Python", "JavaScript", "C#", "Go"));
    data.put("popularity", Arrays.asList(16.8, 36.2, 9.2, 6.3, 4.2));
    data.put("year_created", Arrays.asList(1995, 1991, 1995, 2000, 2009));

    sampleDataFrame = DS.fromMap(data);
  }

  @Test
  void testCreate() {
    // Test creating a DataFrame from columns
    String[] columns = { "col1", "col2" };
    List<Integer> col1Data = Arrays.asList(1, 2, 3);
    List<String> col2Data = Arrays.asList("a", "b", "c");

    DataFrame df = DS.create(columns, col1Data, col2Data);

    // Verify structure
    assertThat(df.height()).isEqualTo(3);
    assertThat(df.width()).isEqualTo(2);
    assertThat(df.getColumnsIndex().get(0)).isEqualTo("col1");
    assertThat(df.getColumnsIndex().get(1)).isEqualTo("col2");

    // Check individual cell values
    assertThat(df.getColumn("col1").get(0)).isEqualTo(1);
    assertThat(df.getColumn("col2").get(0)).isEqualTo("a");
    assertThat(df.getColumn("col1").get(1)).isEqualTo(2);
    assertThat(df.getColumn("col2").get(1)).isEqualTo("b");
    assertThat(df.getColumn("col1").get(2)).isEqualTo(3);
    assertThat(df.getColumn("col2").get(2)).isEqualTo("c");
  }

  @Test
  void testFromMap() {
    Map<String, List<?>> data = new HashMap<>();
    data.put("A", Arrays.asList(1, 2, 3));
    data.put("B", Arrays.asList("x", "y", "z"));

    DataFrame df = DS.fromMap(data);

    // Verify structure
    assertThat(df.height()).isEqualTo(3);
    assertThat(df.width()).isEqualTo(2);
    assertThat(df.getColumnsIndex().contains("A")).isTrue();
    assertThat(df.getColumnsIndex().contains("B")).isTrue();

    // Check individual cell values
    assertThat(df.getColumn("A").get(0)).isEqualTo(1);
    assertThat(df.getColumn("B").get(0)).isEqualTo("x");
    assertThat(df.getColumn("A").get(1)).isEqualTo(2);
    assertThat(df.getColumn("B").get(1)).isEqualTo("y");
    assertThat(df.getColumn("A").get(2)).isEqualTo(3);
    assertThat(df.getColumn("B").get(2)).isEqualTo("z");
  }

  @Test
  void testSort() {
    // Verify the initial order of the DataFrame
    assertThat(sampleDataFrame.height()).isEqualTo(5);

    // Sort by popularity (descending)
    DataFrame sorted = DS.sort(sampleDataFrame, "popularity", false);

    assertThat(sorted.height()).isEqualTo(5);

    // Python should be first (highest popularity)
    assertThat(sorted.getColumn("name").get(0)).isEqualTo("Python");

    // Go should be last (lowest popularity)
    assertThat(sorted.getColumn("name").get(4)).isEqualTo("Go");

    // Verify the order of popularity values (descending)
    Series<?> popularitySeries = sorted.getColumn("popularity");
    for (int i = 0; i < popularitySeries.size() - 1; i++) {
      double current = ((Number) popularitySeries.get(i)).doubleValue();
      double next = ((Number) popularitySeries.get(i + 1)).doubleValue();
      assertThat(current).isGreaterThanOrEqualTo(next);
    }
  }

  @Test
  void testCsvReadWrite() throws IOException {
    // Create a temporary file for testing
    Path tempFile = Files.createTempFile("test-df", ".csv");
    try {
      // Write the DataFrame to CSV
      DS.toCsv(sampleDataFrame, tempFile.toString());

      // Read it back
      DataFrame readDf = DS.read(tempFile.toString());

      // Verify the data was preserved
      assertThat(readDf.height()).isEqualTo(sampleDataFrame.height());
      assertThat(readDf.width()).isEqualTo(sampleDataFrame.width());

      // Check that column names are preserved
      for (String col : sampleDataFrame.getColumnsIndex()) {
        assertThat(readDf.getColumnsIndex().contains(col)).isTrue();
      }

      // Note: We can't directly compare values because CSV reading
      // might change types (e.g., numbers become strings)

    } finally {
      // Clean up
      Files.deleteIfExists(tempFile);
    }
  }

  @Test
  void testDescribeDoesNotThrowException() {
    // This test verifies that describe() doesn't throw exceptions
    assertThatCode(() -> {
      DS.describe(sampleDataFrame);
    }).doesNotThrowAnyException();
  }

  @Test
  void testShowDoesNotThrowException() {
    // This test verifies that show() doesn't throw exceptions
    assertThatCode(() -> {
      DS.show(sampleDataFrame);
      DS.show(sampleDataFrame, 2);
    }).doesNotThrowAnyException();
  }
}