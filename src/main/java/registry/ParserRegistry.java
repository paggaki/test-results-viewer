package registry;

import interfaces.ReportParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import parsers.JMeterXmlParser;
import parsers.JUnitReportParser;
import utils.XmlUtils;

public class ParserRegistry {

  private final List<ReportParser> parsers = new ArrayList<>();

  public ParserRegistry() {
    parsers.add(new JMeterXmlParser());
    parsers.add(new JUnitReportParser());
  }

  public ReportParser select(InputStream is) throws IOException {
    for (ReportParser parser : parsers) {
      if (parser.supports(is)) {
        XmlUtils.reset(is);
        return parser;
      }
      XmlUtils.reset(is);
    }
    throw new IllegalArgumentException("No parser found for this report format");
  }
}
