package publisher;

import models.TestSuite;
import tools.jackson.databind.ObjectMapper;

public class TestResultPublisher {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String publish(TestSuite testSuite) {
    return objectMapper.writeValueAsString(testSuite);
  }
}
