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

public class JUnitReportParser implements ReportParser {

  @Override
  public boolean supports(InputStream xml) {
    try {
      XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xml);

      while (reader.hasNext()) {
        int event = reader.next();
        if (event == XMLStreamConstants.START_ELEMENT) {
          String root = reader.getLocalName();
          return root.equals("testsuite") || root.equals("testsuites");
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
            case "testsuite":
              currentSuite = new TestSuite();
              currentSuite.setName(reader.getAttributeValue(null, "name"));
              currentSuite.setTestCases(new ArrayList<>());
              suites.add(currentSuite);
              break;

            case "testcase":
              currentCase = new TestCase();
              currentCase.setName(reader.getAttributeValue(null, "name"));

              String time = reader.getAttributeValue(null, "time");
              if (time != null) {
                currentCase.setDuration((long) (Double.parseDouble(time) * 1000));
              }

              currentCase.setStatus(TestStatus.PASSED);
              break;

            case "failure":
              if (currentCase != null) {
                currentCase.setStatus(TestStatus.FAILED);
                currentCase.setErrorMessage(reader.getAttributeValue(null, "message"));
                if (reader.hasNext()) {
                  reader.next();
                  if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
                    currentCase.setStackTrace(reader.getText().trim());
                  }
                }
              }
              break;

            case "error":
              if (currentCase != null) {
                currentCase.setStatus(TestStatus.ERROR);
                currentCase.setErrorMessage(reader.getAttributeValue(null, "message"));
              }
              break;

            case "skipped":
              if (currentCase != null) {
                currentCase.setStatus(TestStatus.SKIPPED);
              }
              break;
          }
        } else if (event == XMLStreamConstants.END_ELEMENT) {

          if (reader.getLocalName().equals("testcase")) {
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
