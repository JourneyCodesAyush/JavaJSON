package io.github.journeycodesayush.javajson.parser;

import io.github.journeycodesayush.javajson.parser.JsonValue.*;

import java.util.Map;

public class JsonAstPrinter implements JsonValue.Visitor<String> {

    private int indentLevel = 0;
    private String INDENT = "  ";

    private String indent() {
        return INDENT.repeat(indentLevel);
    }

    public String print(JsonValue value) {
        indentLevel = 0;
        return value.accept(this);
    }

    @Override
    public String visitJsonArray(JsonArray jsonvalue) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        sb.append("[");

        for (JsonValue value : jsonvalue.elements()) {
            if (!first) {
                sb.append(",\n");
            }

            first = false;

            sb.append(value.accept(this));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String visitJsonObject(JsonObject jsonvalue) {
        StringBuilder sb = new StringBuilder();

        indentLevel++;
        boolean first = true;

        sb.append("{\n");
        for (Map.Entry<String, JsonValue> entry : jsonvalue.members().entrySet()) {

            if (!first) {
                sb.append(",\n");
            }
            first = false;

            String key = entry.getKey();
            JsonValue value = entry.getValue();

            sb.append(indent()).append("\"").append(key).append("\": ");

            sb.append(value.accept(this));
        }
        indentLevel--;
        sb.append("\n").append(indent()).append("}");
        return sb.toString();
    }

    @Override
    public String visitJsonString(JsonString jsonvalue) {
        return "\"" + jsonvalue.value() + "\"";
    }

    @Override
    public String visitJsonNumber(JsonNumber jsonvalue) {
        double v = jsonvalue.value();

        return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
    }

    @Override
    public String visitJsonBoolean(JsonBoolean jsonvalue) {
        return String.valueOf(jsonvalue.value() == true);
    }

    @Override
    public String visitJsonNull(JsonNull jsonvalue) {
        return "null";
    }
}
