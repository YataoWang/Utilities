package utilities.common;

import java.math.BigDecimal;

public class JsonWriter extends TextWriter {
  private StringBuilder json;

  public JsonWriter() {
    this.json = new StringBuilder();
  }

  public String toString() {
    return this.json.toString();
  }

  public JsonWriter startObject() {
    this.json.append("{");
    return this;
  }

  public JsonWriter endObject() {
    this.json.append("}");
    return this;
  }

  public JsonWriter startArray() {
    this.json.append("[");
    return this;
  }

  public JsonWriter endArray() {
    this.json.append("]");
    return this;
  }

  public JsonWriter writeSeparator() {
    this.json.append(",");
    return this;
  }

  public JsonWriter writeColon() {
    this.json.append(":");
    return this;
  }

  public JsonWriter writeName(String name) {
    this.json.append(name);
    return this;
  }

  public JsonWriter writeNull() {
    this.json.append("null");
    return this;
  }

  public JsonWriter writeString(String value) {
    return writeString(value, true);
  }

  public JsonWriter writeString(String value, boolean quote) {
    if (null == value) {
      return writeNull();
    }
    if (quote) {
      value = quoteValue(value);
    }
    this.json.append(value);
    return this;
  }

  public JsonWriter writeChar(char value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeBool(boolean value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeByte(byte value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeShort(short value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeInt(int value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeLong(long value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeFloat(float value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeDouble(double value) {
    this.json.append(value);
    return this;
  }

  public JsonWriter writeDecimal(BigDecimal value) {
    if (value.scale() > 255) {
      this.json.append(value.toEngineeringString());
    } else {
      this.json.append(value.toPlainString());
    }
    return this;
  }
}



