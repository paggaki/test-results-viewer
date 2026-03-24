package utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class XmlUtils {

  private XmlUtils() {}

  public static XMLInputFactory newSecureInputFactory() {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
    factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    return factory;
  }

  public static XMLStreamReader createReader(InputStream input) throws XMLStreamException {

    return newSecureInputFactory().createXMLStreamReader(input);
  }

  public static String rootElementName(InputStream input) {
    try {
      XMLStreamReader reader = createReader(input);

      while (reader.hasNext()) {
        if (reader.next() == XMLStreamConstants.START_ELEMENT) {
          return reader.getLocalName();
        }
      }
    } catch (Exception ignored) {
    }

    return null;
  }

  public static String getAttribute(XMLStreamReader reader, String name) {
    return reader.getAttributeValue(null, name);
  }

  public static String getAttributeOrDefault(
      XMLStreamReader reader, String name, String defaultValue) {
    String value = reader.getAttributeValue(null, name);
    return value != null ? value : defaultValue;
  }

  public static double getDoubleAttribute(
      XMLStreamReader reader, String name, double defaultValue) {
    String value = reader.getAttributeValue(null, name);
    if (value == null) return defaultValue;
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static String readElementText(XMLStreamReader reader) throws XMLStreamException {
    StringBuilder text = new StringBuilder();
    while (reader.hasNext()) {
      int event = reader.next();
      if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA) {
        text.append(reader.getText());
      } else if (event == XMLStreamConstants.END_ELEMENT) {
        break;
      }
    }

    return text.toString().trim();
  }

  public static InputStream toResettableStream(InputStream input) throws IOException {
    if (input.markSupported()) return input;
    BufferedInputStream buffered = new BufferedInputStream(input);
    buffered.mark(Integer.MAX_VALUE);
    return buffered;
  }

  public static void reset(InputStream input) throws IOException {
    if (input.markSupported()) {
      input.reset();
    }
  }
}
