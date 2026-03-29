package registry;

import interfaces.ReportParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import kafka.Topics;
import parsers.JMeterXmlParser;
import parsers.JUnitReportParser;
import utils.XmlUtils;

public class ParserRegistry {

  private final Map<ReportParser, String> parsers = new LinkedHashMap<>();

  public ParserRegistry() {
    parsers.put(new JMeterXmlParser(), Topics.JMETER_REPORTS.toString());
    parsers.put(new JUnitReportParser(), Topics.JUNIT_REPORTS.toString());
  }

  public ReportParser select(InputStream is) throws IOException {
    for (ReportParser parser : parsers.keySet()) {
      if (parser.supports(is)) {
        XmlUtils.reset(is);
        return parser;
      }
      XmlUtils.reset(is);
    }
    throw new IllegalArgumentException("No parser found for this report format");
  }

  public String selectTopic(ReportParser parser) {
    for (Map.Entry<ReportParser, String> entry : parsers.entrySet()) {
      if (entry.getKey().equals(parser)) {
        return entry.getValue();
      }
    }
    throw new IllegalArgumentException("No topic found for this report format");
  }
}
