package io.github.journeycodesayush.javajson.query;

import io.github.journeycodesayush.javajson.parser.JsonValue;
import java.util.List;

/**
 * Evaluates a parsed JSON path against a {@link JsonValue} tree.
 *
 * <p>Walks the tree guided by a list of {@link PathSegment} objects, returning the value at the
 * specified path, or {@link JsonValue.JsonNull} if the path does not exist.
 */
public class JsonQuery {
  /**
   * Evaluates the given path segments against the root {@link JsonValue}.
   *
   * @param root the root {@link JsonValue} to query
   * @param segments the list of {@link PathSegment} objects representing the path
   * @return the {@link JsonValue} at the specified path, or {@link JsonValue.JsonNull} if the path
   *     does not resolve to a value
   */
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
