import interfaces.ReportParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import kafka.KafkaProducerApp;
import models.TestCase;
import models.TestReport;
import models.TestStatus;
import models.TestSuite;
import registry.ParserRegistry;
import utils.XmlUtils;

public class ReportParserApp {
  public static void main(String[] args) throws IOException {
    InputStream is =
        XmlUtils.toResettableStream(
            Files.newInputStream(Path.of("src/main/resources/report-samples/" + args[0])));
    ParserRegistry registry = new ParserRegistry();
    ReportParser parser = registry.select(is);
    TestReport report = parser.parse(is);

    List<TestSuite> testSuites = report.getTestSuites();
    for (TestSuite testSuite : testSuites) {
      List<TestCase> testCases = testSuite.getTestCases();
      long passed = testCases.stream().filter(tc -> tc.getStatus() == TestStatus.PASSED).count();
      long failed = testCases.stream().filter(tc -> tc.getStatus() == TestStatus.FAILED).count();
      long skipped = testCases.stream().filter(tc -> tc.getStatus() == TestStatus.SKIPPED).count();
      long errors = testCases.stream().filter(tc -> tc.getStatus() == TestStatus.ERROR).count();

      String stackTraces =
          testCases.stream()
              .filter(tc -> tc.getStackTrace() != null)
              .map(tc -> tc.getName() + ": " + tc.getStackTrace())
              .collect(Collectors.joining(" | "));

      String results =
          "Test Suite: "
              + testSuite.getName()
              + " | Pass: "
              + passed
              + " | Fail: "
              + failed
              + " | Skipped: "
              + skipped
              + " | Errors: "
              + errors
              + " | Stack Trace: "
              + stackTraces
              + " | Total Tests: "
              + testCases.size();

      KafkaProducerApp.send(registry.selectTopic(parser), report.getEndTime(), results);
      System.out.println(results);
    }
  }
}
