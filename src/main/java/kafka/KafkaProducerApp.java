package kafka;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducerApp {

  public static void send(String topic, String key, String message) throws IOException {
    Properties props = new Properties();
    props.load(new FileReader("src/main/resources/kafka/producer.properties"));

    try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
      ProducerRecord<String, String> record =
          new ProducerRecord<>(topic, String.valueOf(key), message);
      producer.send(
          record,
          (metadata, exception) -> {
            if (exception != null) {
              System.err.println("Error sending record: " + exception.getMessage());
              exception.printStackTrace();
            } else {
              System.out.println(
                  "Record sent successfully to topic "
                      + metadata.topic()
                      + " partition "
                      + metadata.partition()
                      + " at offset "
                      + metadata.offset());
            }
          });
    } catch (Exception e) {
      System.err.println("Error creating or sending producer: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
