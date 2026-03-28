package parsers;

import interfaces.ReportParser;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import models.TestCase;
import models.TestReport;
import models.TestStatus;
import models.TestSuite;

public class JMeterXmlParser implements ReportParser {

  @Override
  public boolean supports(InputStream xml) {
    try {
      XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xml);

      while (reader.hasNext()) {
        int event = reader.next();
        if (event == XMLStreamConstants.START_ELEMENT) {
          String root = reader.getLocalName();
          return root.equals("testResults");
        }
      }
    } catch (Exception ignored) {
    }
    return false;
  }

  @Override
  public TestReport parse(InputStream xml) {
    TestReport report = new TestReport();
    List<TestSuite> suites = new ArrayList<>();

    try {
      XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xml);

      TestSuite currentSuite = null;
      TestCase currentCase = null;

      while (reader.hasNext()) {
        int event = reader.next();

        if (event == XMLStreamConstants.START_ELEMENT) {

          switch (reader.getLocalName()) {
            case "testResults":
              currentSuite = new TestSuite();
              String version = reader.getAttributeValue(null, "version");
              currentSuite.setName("JMeter Results" + (version != null ? " v" + version : ""));
              currentSuite.setTestCases(new ArrayList<>());
              suites.add(currentSuite);
              break;

            case "httpSample", "sample":
              currentCase = new TestCase();
              currentCase.setName(reader.getAttributeValue(null, "lb"));
              currentCase.setResult(reader.getAttributeValue(null, "rm"));

              String time = reader.getAttributeValue(null, "t");
              if (time != null) {
                currentCase.setDuration(Long.parseLong(time));
              }

              boolean success = "true".equals(reader.getAttributeValue(null, "s"));
              currentCase.setStatus(success ? TestStatus.PASSED : TestStatus.FAILED);
              break;
          }
        } else if (event == XMLStreamConstants.END_ELEMENT) {

          if (reader.getLocalName().equals("httpSample")
              || reader.getLocalName().equals("sample")) {
            if (currentSuite != null && currentCase != null) {
              currentSuite.getTestCases().add(currentCase);
            }
            currentCase = null;
          }
        }
      }

      report.setTestSuites(suites);
      report.setStartTime(Instant.now());
      report.setEndTime(Instant.now());

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JUnit report", e);
    }

    return report;
  }
}
