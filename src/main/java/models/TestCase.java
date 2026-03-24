package models;

public class TestCase {

  private String name;
  private TestStatus status;
  private long duration;
  private String errorMessage;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TestStatus getStatus() {
    return status;
  }

  public void setStatus(TestStatus status) {
    this.status = status;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
