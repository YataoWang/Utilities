package utilities.common;

public class TextWriter {
  protected String quoteValue(String text) {
    return quoteValue(text, "\"", "\"");
  }

  protected String quoteValue(String text, String openQuote, String closeQuote) {
    return new StringBuilder().append(openQuote).append(text).append(closeQuote).toString();
  }
}