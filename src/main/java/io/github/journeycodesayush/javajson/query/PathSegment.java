package io.github.journeycodesayush.javajson.query;

/**
 * Represents a single segment in a JSON path expression.
 *
 * <p>A path segment is either a {@link Key} (object field access) or an {@link Index} (array index
 * access).
 */
public sealed interface PathSegment permits PathSegment.Key, PathSegment.Index {
  /**
   * Represents an object field access by key name.
   *
   * @param name the field name to access
   */
  record Key(String name) implements PathSegment {}

  /**
   * Represents an array element access by index.
   *
   * @param index the zero-based array index to access
   */
  record Index(int index) implements PathSegment {}
}
