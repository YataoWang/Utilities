package utilities.common;

import java.math.BigDecimal;
import java.util.Stack;

public class XmlWriter extends TextWriter {
  private StringBuilder xml;
  private StringBuilder current;
  private Stack<String> elements;
  private boolean currentValue;

  public XmlWriter() {
    this.xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    this.current = new StringBuilder();
    this.elements = new Stack<String>();
  }

  public String toString() {
    return this.xml.toString();
  }

  public XmlWriter startElement(String element) {
    return startElement(null, element);
  }

  public XmlWriter startElement(String namespace, String element) {
    String elementName = join(namespace, element);
    this.elements.push(elementName);
    if (this.current.length() > 0) {
      this.xml.append(this.current).append(">");
      this.current = new StringBuilder();
    }
    this.current.append("<").append(elementName);
    this.currentValue = false;
    return this;
  }

  public XmlWriter endElement() {
    String element = this.elements.pop();
    if (this.current.length() > 0) {
      if (this.currentValue) {
        this.current.append("</").append(element).append(">");
      } else {
        this.current.append("/>");
      }
      this.xml.append(this.current);
      this.current = new StringBuilder();
    } else {
      this.xml.append("</").append(element).append(">");
    }
    this.currentValue = false;
    return this;
  }

  public XmlWriter writeAttribute(String name, String value) {
    return writeAttribute(null, name, value);
  }

  public XmlWriter writeAttribute(String namespace, String name, String value) {
    this.current.append(" ").append(join(namespace, name)).append("=").append(quoteValue(value));
    return this;
  }

  public XmlWriter writeString(String value) {
    return writeString(value, false);
  }

  public XmlWriter writeString(String value, boolean quote) {
    if (null == value) {
      value = "";
    } else if (quote) {
      value = quoteValue(value);
    }
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeChar(char value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeBool(boolean value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeByte(byte value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeShort(short value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeInt(int value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeLong(long value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeFloat(float value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeDouble(double value) {
    this.current.append(">").append(value);
    this.currentValue = true;
    return this;
  }

  public XmlWriter writeDecimal(BigDecimal value) {
    this.current.append(">");
    if (value.scale() > 255) {
      this.current.append(value.toEngineeringString());
    } else {
      this.current.append(value.toPlainString());
    }
    this.currentValue = true;
    return this;
  }

  private String join(String namespace, String element) {
    if (null == namespace || namespace.length() <= 0) {
      return element;
    }

    return new StringBuilder().append(namespace).append(":").append(element).toString();
  }
}