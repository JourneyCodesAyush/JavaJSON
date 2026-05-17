package io.github.journeycodesayush.javajson.parser;

import io.github.journeycodesayush.javajson.parser.JsonValue.*;

import java.util.Map;

public class JsonAstPrinter implements JsonValue.Visitor<String> {

    public enum PrintMode {
        MINIFY,
        PRETTY
    }

    private int indentLevel = 0;
    private String INDENT = "  ";
    private final PrintMode printMode;

    public JsonAstPrinter() {
        this.printMode = PrintMode.PRETTY;
    }

    public JsonAstPrinter(PrintMode printMode) {
        this.printMode = printMode;
    }

    private String newline() {
        return printMode == PrintMode.PRETTY ? "\n" : "";
    }

    private String space() {
        return printMode == PrintMode.PRETTY ? " " : "";
    }

    private String indent() {
        return INDENT.repeat(indentLevel);
    }

    public String print(JsonValue value) {
        indentLevel = 0;
        return value.accept(this);
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public String visitJsonArray(JsonArray jsonvalue) {
        if (jsonvalue.elements().isEmpty())
            return "[]";

        StringBuilder sb = new StringBuilder();

        sb.append("[").append(newline());
        indentLevel++;

        boolean first = true;

        for (JsonValue value : jsonvalue.elements()) {
            if (!first)
                sb.append(",").append(newline());
            first = false;

            sb.append(indent())
                    .append(value.accept(this));
        }

        indentLevel--;
        sb.append(newline());

        if (printMode == PrintMode.PRETTY)
            sb.append(indent());

        sb.append("]");

        return sb.toString();
    }

    @Override
    public String visitJsonObject(JsonObject jsonvalue) {
        if (jsonvalue.members().isEmpty())
            return "{}";

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

        if (printMode == PrintMode.PRETTY)
            sb.append(indent());

        sb.append("}");
        return sb.toString();
    }

    @Override
    public String visitJsonString(JsonString jsonvalue) {
        return "\"" + escape(jsonvalue.value()) + "\"";
    }

    @Override
    public String visitJsonNumber(JsonNumber jsonvalue) {
        double v = jsonvalue.value();

        return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
    }

    @Override
    public String visitJsonBoolean(JsonBoolean jsonvalue) {
        return String.valueOf(jsonvalue.value());
    }

    @Override
    public String visitJsonNull(JsonNull jsonvalue) {
        return "null";
    }
}
