# Test Report Parser

A Java library that parses test reports from multiple frameworks into a common normalized model, with Kafka integration
for streaming results downstream.

---

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── interfaces/
│   │   │   └── ReportParser.java             # Parser contract
│   │   ├── models/
│   │   │   ├── TestReport.java
│   │   │   ├── TestSuite.java
│   │   │   ├── TestCase.java
│   │   │   └── TestStatus.java               # PASSED, FAILED, ERROR, SKIPPED
│   │   ├── parsers/
│   │   │   ├── JUnitReportParser.java         # JUnit / TestNG / Surefire / Playwright XML
│   │   │   └── JMeterXmlParser.java           # JMeter JTL XML
│   │   ├── registry/
│   │   │   └── ParserRegistry.java            # Auto-detects parser + resolves Kafka topic
│   │   ├── utils/
│   │   │   └── XmlUtils.java                 # Resettable stream helpers
│   │   └── ReportParserApp.java              # Entry point
│   └── resources/
│       └── report-samples/
│           ├── junit/
│           ├── jmeter/
│           └── playwright/
```

---

## Supported Formats

| Framework   | Format                  | Status  |
|-------------|-------------------------|---------|
| JUnit 4 / 5 | Surefire XML            | Done    |
| TestNG      | Surefire-compatible XML | Done    |
| Surefire    | Surefire XML            | Done    |
| Playwright  | JUnit XML               | Done    |
| JMeter      | JTL XML                 | Done    |
| JMeter      | JTL CSV                 | Planned |
| RestAssured | JUnit XML               | Planned |
| Gatling     | JSON / HTML             | Planned |

---

## How It Works

All parsers implement the `ReportParser` interface:

```java
public interface ReportParser {
    boolean supports(InputStream xml);  // detect if this parser handles the format
    TestReport parse(InputStream xml);  // parse and return normalized result
}
```

`ParserRegistry` holds all registered parsers mapped to their Kafka topic. It auto-detects
the correct parser by calling `supports()` on each one in registration order, and returns
both the parser and the resolved topic together:

```java
ParserRegistry registry = new ParserRegistry();
ParserRegistry.ParserResult result = registry.select(is);

TestReport report = result.parser().parse(is);
String topic = result.topic();
```

Adding a new parser requires two steps — implement `ReportParser` and register it in
`ParserRegistry` with its target topic. No other changes needed.

---

## Kafka Topics

Each parser is registered with a dedicated Kafka topic in `ParserRegistry`:

| Parser              | Topic                 |
|---------------------|-----------------------|
| `JUnitReportParser` | `test.results.junit`  |
| `JMeterXmlParser`   | `test.results.jmeter` |

Topics are resolved automatically from the registry — no manual topic selection needed in
application code.

---

## Running the App

Pass the report path relative to `src/main/resources/report-samples/` as a program argument:

```bash
mvn exec:java -Dexec.mainClass="ReportParserApp" -Dexec.args="jmeter/sample_jmeter_results.xml"
mvn exec:java -Dexec.mainClass="ReportParserApp" -Dexec.args="junit/junit-mixed-results.xml"
```

The app will auto-detect the format, parse the report, print a summary per suite, and
(once the Kafka producer is wired) publish each `TestSuite` as a JSON message to the
resolved topic.

---

## Sample Reports

Located under `src/main/resources/report-samples/`:

## Adding a New Parser

1. Create a class in `parsers/` implementing `ReportParser`
2. Implement `supports()` to detect your format
3. Implement `parse()` to return a normalized `TestReport`
4. Register it in `ParserRegistry` with its Kafka topic

```java
public ParserRegistry() {
    parsers.put(new JMeterXmlParser(), "test.results.jmeter");
    parsers.put(new JUnitReportParser(), "test.results.junit");
    // parsers.put(new JMeterCsvParser(), "test.results.jmeter");
    // parsers.put(new GatlingParser(),   "test.results.gatling");
}
```

Registration order matters — more specific parsers should be registered first.

---

## Roadmap

- [x] JUnit / TestNG / Surefire XML parser
- [x] Playwright XML parser (via JUnit XML format)
- [x] JMeter XML parser
- [x] ParserRegistry with auto-detection and topic resolution
- [x] CLI argument for report path
- [ ] JMeter CSV parser
- [ ] Gatling parser
- [ ] Kafka producer — publish each `TestSuite` as JSON to resolved topic
- [ ] Prometheus metrics endpoint
- [ ] Grafana dashboards

---
