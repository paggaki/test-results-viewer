package models;

import java.time.Instant;
import java.util.List;

public class TestReport {
  private String name;
  private Instant startTime;
  private Instant endTime;
  private List<TestSuite> testSuites;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  public Instant getEndTime() {
    return endTime;
  }

  public void setEndTime(Instant endTime) {
    this.endTime = endTime;
  }

  public List<TestSuite> getTestSuites() {
    return testSuites;
  }

  public void setTestSuites(List<TestSuite> testSuites) {
    this.testSuites = testSuites;
  }
}
