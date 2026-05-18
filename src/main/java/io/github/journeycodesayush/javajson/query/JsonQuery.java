package io.github.journeycodesayush.javajson.query;

import java.util.List;

import io.github.journeycodesayush.javajson.parser.JsonValue;

public class JsonQuery {
    public static JsonValue get(JsonValue root, List<PathSegment> segments) {
        JsonValue current = root;
        for (PathSegment segment : segments) {
            switch (segment) {
                case PathSegment.Key k -> {
                    if (current instanceof JsonValue.JsonObject object) {
                        // System.out.println(k.name());
                        current = object.members().get(k.name());
                    }
                }
                case PathSegment.Index i -> {
                    if (current instanceof JsonValue.JsonArray array) {
                        // System.out.println(i.index());
                        current = array.elements().get(i.index());
                    }
                }
            }
        }
        return current == null ? new JsonValue.JsonNull() : current;
    }
}
