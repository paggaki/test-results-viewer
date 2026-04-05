import interfaces.ReportParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import kafka.KafkaProducerApp;
import models.TestReport;
import models.TestSuite;
import publisher.TestResultPublisher;
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
    String kafkaTopic = registry.selectTopic(parser);
    List<TestSuite> testSuites = report.getTestSuites();

    for (TestSuite testSuite : testSuites) {
      String kafkaKey = testSuite.getName();
      String results = TestResultPublisher.publish(testSuite);
      KafkaProducerApp.send(kafkaTopic, kafkaKey, results);
      System.out.println(results);
    }
  }
}
