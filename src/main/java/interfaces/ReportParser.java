package interfaces;

import java.io.InputStream;
import models.TestReport;

public interface ReportParser {
  boolean supports(InputStream xml);

  TestReport parse(InputStream xml);
}
