import interfaces.ReportParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import models.TestReport;
import parsers.JUnitReportParser;
import utils.XmlUtils;

public class ReportParserApp {
  public static void main(String[] args) throws IOException {
    ReportParser parser = new JUnitReportParser();
    InputStream is =
        XmlUtils.toResettableStream(
            Files.newInputStream(Path.of("src/main/resources/junit-test-report.xml")));

    try (is) {
      if (parser.supports(is)) {
        XmlUtils.reset(is);
        TestReport report = parser.parse(is);

        System.out.println("Suites: " + report.getTestSuites().getFirst().getName());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
