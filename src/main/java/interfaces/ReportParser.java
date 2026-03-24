package interfaces;

import models.TestReport;

import java.io.InputStream;

public interface ReportParser {
    boolean supports(InputStream xml);

    TestReport parse(InputStream xml);
}
