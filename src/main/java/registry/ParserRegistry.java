package registry;

import interfaces.ReportParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import parsers.JMeterXmlParser;
import parsers.JUnitReportParser;
import utils.XmlUtils;

public class ParserRegistry {

  private final Map<ReportParser, String> parsers = new HashMap<>();

  public ParserRegistry() {
    parsers.put(new JMeterXmlParser(), "JMeterXML Topic");
    parsers.put(new JUnitReportParser(), "JUnit Topic");
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
}
