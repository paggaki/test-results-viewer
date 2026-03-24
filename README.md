# Test Report Parser

A Java library that parses test reports from multiple frameworks into a common normalized model.

---

## Project Structure

```
src/main/java/
├── interfaces/
│   └── ReportParser.java         # Parser contract
├── models/
│   ├── TestReport.java
│   ├── TestSuite.java
│   ├── TestCase.java
│   └── TestStatus.java           # PASSED, FAILED, ERROR, SKIPPED
├── parsers/
│   └── JUnitReportParser.java    # Handles JUnit / TestNG / Surefire XML
└── utils/
    └── XmlUtils.java             # Resettable stream helpers
```

---

## Supported Formats

| Framework             | Format                  | Status  |
|-----------------------|-------------------------|---------|
| JUnit 4 / 5           | Surefire XML            | Done    |
| TestNG                | Surefire-compatible XML | Done    |
| RestAssured           | JUnit XML               | Planned |
| JMeter                | JTL / CSV               | Planned |
| Selenium / Playwright | JUnit XML               | Planned |

---

## How It Works

All parsers implement the `ReportParser` interface:

```java
public interface ReportParser {
    boolean supports(InputStream xml);  // detect if this parser handles the format

    TestReport parse(InputStream xml);  // parse and return normalized result
}
```

The `ParserRegistry` auto-detects the correct parser — no need to specify the format manually:

```java
ParserRegistry registry = new ParserRegistry();
TestReport report = registry.parse(Files.newInputStream(Path.of("report.xml")));
```

---

## Data Model

### TestReport

| Field      | Type              | Description                               |
|------------|-------------------|-------------------------------------------|
| testSuites | `List<TestSuite>` | All suites in the report                  |
| startTime  | `Instant`         | Parsed from `timestamp` attribute         |
| endTime    | `Instant`         | Derived from `startTime + total duration` |

### TestSuite

| Field     | Type             | Description                  |
|-----------|------------------|------------------------------|
| name      | `String`         | Suite class name             |
| testCases | `List<TestCase>` | All test cases in this suite |

### TestCase

| Field        | Type         | Description                            |
|--------------|--------------|----------------------------------------|
| name         | `String`     | Test method name                       |
| status       | `TestStatus` | `PASSED`, `FAILED`, `ERROR`, `SKIPPED` |
| duration     | `long`       | Execution time in milliseconds         |
| errorMessage | `String`     | Short failure message                  |
| stackTrace   | `String`     | Full stack trace body                  |

---

## Adding a New Parser

1. Create a class implementing `ReportParser`
2. Implement `supports()` to detect your format
3. Implement `parse()` to return a `TestReport`
4. Register it in `ParserRegistry`

---

## Sample Reports

Located under `src/main/resources/`:

| File                         | Description                                           |
|------------------------------|-------------------------------------------------------|
| `junit-all-passing.xml`      | All tests pass                                        |
| `junit-mixed-results.xml`    | All 4 statuses with stack traces                      |
| `junit-multiple-suites.xml`  | `<testsuites>` wrapper with multiple suites           |
| `junit-edge-cases.xml`       | Empty suite, missing attributes, no message attribute |
| `surefire-all-passing.xml`   | Surefire format with `<properties>` block             |
| `surefire-mixed-results.xml` | Includes `<system-out>` and `<system-err>` blocks     |
| `testng-all-passing.xml`     | TestNG `status` attribute on `<testcase>`             |
| `testng-mixed-results.xml`   | TestNG multi-suite with failures and skips            |

---

## Roadmap

- [x] JUnit / TestNG / Surefire XML parser
- [x] ParserRegistry with auto-detection
- [ ] JMeter JTL parser
- [ ] Selenium / Playwright parser
- [ ] Kafka producer integration