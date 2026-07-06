package io.github.journeycodesayush.javajson.parser;

import io.github.journeycodesayush.javajson.parser.JsonValue.*;
import java.util.Map;

/**
 * Prints a {@link JsonValue} tree as a formatted JSON string.
 *
 * <p>Supports two modes: {@link PrintMode#PRETTY} for human-readable output with indentation, and
 * {@link PrintMode#MINIFY} for compact output.
 */
public class JsonAstPrinter implements JsonValue.Visitor<String> {

  /** Controls the output format of the printer. */
  public enum PrintMode {
    MINIFY,
    PRETTY
  }

  /** Current indentation level, incremented for each nested structure. */
  private int indentLevel = 0;

  /** The indentation string used per level in pretty print mode. */
  private String INDENT = "  ";

  /** The print mode — either {@link PrintMode#PRETTY} or {@link PrintMode#MINIFY}. */
  private final PrintMode printMode;

  /** Constructs a printer in {@link PrintMode#PRETTY} mode. */
  public JsonAstPrinter() {
    this.printMode = PrintMode.PRETTY;
  }

  /**
   * Constructs a printer with the given print mode.
   *
   * @param printMode the {@link PrintMode} to use
   */
  public JsonAstPrinter(PrintMode printMode) {
    this.printMode = printMode;
  }

  /**
   * Returns a newline character in pretty mode, or empty string in minify mode.
   *
   * @return newline or empty string
   */
  private String newline() {
    return printMode == PrintMode.PRETTY ? "\n" : "";
  }

  /**
   * Returns a space character in pretty mode, or empty string in minify mode.
   *
   * @return space or empty string
   */
  private String space() {
    return printMode == PrintMode.PRETTY ? " " : "";
  }

  /**
   * Returns the current indentation string based on the indent level.
   *
   * @return the indentation string
   */
  private String indent() {
    return INDENT.repeat(indentLevel);
  }

  /**
   * Prints the given {@link JsonValue} tree as a JSON string.
   *
   * @param value the root {@link JsonValue} to print
   * @return the JSON string representation
   */
  public String print(JsonValue value) {
    indentLevel = 0;
    return value.accept(this);
  }

  /**
   * Escapes special characters in a string for valid JSON output.
   *
   * @param s the raw string to escape
   * @return the escaped string
   */
  private String escape(String s) {
    return s.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }

  /** {@inheritDoc} */
  @Override
  public String visitJsonArray(JsonArray jsonvalue) {
    if (jsonvalue.elements().isEmpty()) return "[]";

    StringBuilder sb = new StringBuilder();

    sb.append("[").append(newline());
    indentLevel++;

    boolean first = true;

    for (JsonValue value : jsonvalue.elements()) {
      if (!first) sb.append(",").append(newline());
      first = false;

      sb.append(indent()).append(value.accept(this));
    }

    indentLevel--;
    sb.append(newline());

    if (printMode == PrintMode.PRETTY) sb.append(indent());

    sb.append("]");

    return sb.toString();
  }

  /** {@inheritDoc} */
  @Override
  public String visitJsonObject(JsonObject jsonvalue) {
    if (jsonvalue.members().isEmpty()) return "{}";

    StringBuilder sb = new StringBuilder();

    sb.append("{").append(newline());

    indentLevel++;

    boolean first = true;

    for (Map.Entry<String, JsonValue> entry : jsonvalue.members().entrySet()) {

      if (!first) {
        sb.append(",").append(newline());
      }
      first = false;

      String key = entry.getKey();
      JsonValue value = entry.getValue();

      sb.append(indent()).append("\"").append(key).append("\":").append(space());

      sb.append(value.accept(this));
    }
    indentLevel--;
    sb.append(newline());

    if (printMode == PrintMode.PRETTY) sb.append(indent());

    sb.append("}");
    return sb.toString();
  }

  /** {@inheritDoc} */
  @Override
  public String visitJsonString(JsonString jsonvalue) {
    return "\"" + escape(jsonvalue.value()) + "\"";
  }

  /** {@inheritDoc} */
  @Override
  public String visitJsonNumber(JsonNumber jsonvalue) {
    double v = jsonvalue.value();

    return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
  }

  /** {@inheritDoc} */
  @Override
  public String visitJsonBoolean(JsonBoolean jsonvalue) {
    return String.valueOf(jsonvalue.value());
  }

  /** {@inheritDoc} */
  @Override
  public String visitJsonNull(JsonNull jsonvalue) {
    return "null";
  }
}
