package io.github.journeycodesayush.javajson.query;

import io.github.journeycodesayush.javajson.query.PathLexer.PathToken;
import io.github.journeycodesayush.javajson.query.PathLexer.PathTokenType;
import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser for jq-like JSON path expressions.
 *
 * <p>Consumes a list of {@link PathLexer.PathToken} objects and produces a list of {@link
 * PathSegment} objects representing the path.
 *
 * <pre>
 * path    → segment+
 * segment → "." key         (object access)
 *         | "." "[" index "]"
 *         | "[" index "]"   (array access)
 * key     → identifier
 * index   → integer
 * </pre>
 */
public class PathParser {
  /** The list of path tokens to parse. */
  private final List<PathToken> tokens;

  /** Current position in the token list. */
  private int current = 0;

  /** Thrown when the parser encounters an invalid or unexpected path token. */
  public static class PathParseError extends RuntimeException {
    public PathParseError(String message) {
      super("[PATH] " + message);
    }
  }

  /**
   * Constructs a PathParser for the given token list.
   *
   * @param tokens the list of {@link PathLexer.PathToken} objects to parse
   */
  public PathParser(List<PathToken> tokens) {
    this.tokens = tokens;
  }

  /**
   * Checks whether the parser has reached the end of the token stream.
   *
   * @return true if at EOF, false otherwise
   */
  private boolean isAtEnd() {
    return peek().pathTokenType() == PathTokenType.EOF;
  }

  /**
   * Returns the current token without consuming it.
   *
   * @return the current {@link PathToken}
   */
  private PathToken peek() {
    return tokens.get(current);
  }

  /**
   * Returns the most recently consumed token.
   *
   * @return the previous {@link PathToken}
   */
  private PathToken previous() {
    return tokens.get(current - 1);
  }

  /**
   * Consumes the current token and returns it.
   *
   * @return the consumed {@link Token}
   */
  private PathToken advance() {
    if (!isAtEnd()) {
      current++;
    }
    return previous();
  }

  /**
   * Checks if the current token is of the given type without consuming it.
   *
   * @param type the {@link TokenType} to check
   * @return true if it matches, false otherwise
   */
  private boolean check(PathTokenType type) {
    if (isAtEnd()) return false;

    return peek().pathTokenType() == type;
  }

  /**
   * Checks if the current token matches any of the given types and advances if so.
   *
   * @param types one or more {@link TokenType} values to match
   * @return true if matched and advanced, false otherwise
   */
  private boolean match(PathTokenType... types) {
    for (PathTokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  /**
   * Consumes a token of the expected type or throws a {@link ParseError}.
   *
   * @param type the expected {@link PathTokenType}
   * @param message the error message if the type does not match
   * @return the consumed {@link PathToken}
   * @throws ParseError if the current token does not match the expected type
   */
  private PathToken consume(PathTokenType type, String message) {
    if (check(type)) {
      return advance();
    }
    throw new PathParseError(message);
  }

  /**
   * Parses the token list and returns a list of {@link PathSegment} objects.
   *
   * @return the list of path segments
   * @throws PathParseError if the path expression is invalid
   */
  public List<PathSegment> parse() {
    List<PathSegment> pathSegments = path();
    return pathSegments;
  }

  /**
   * Parses all segments in the path expression.
   *
   * @return a list of {@link PathSegment} objects
   */
  private List<PathSegment> path() {
    List<PathSegment> segments = new ArrayList<>();
    while (!isAtEnd()) {
      segments.add(segment());
    }
    return segments;
  }

  /**
   * Parses a single path segment — either a key access or an index access.
   *
   * @return the parsed {@link PathSegment}
   */
  private PathSegment segment() {
    if (match(PathTokenType.DOT)) {
      if (match(PathTokenType.LEFT_BRACKET)) {
        return index();
      }
      return new PathSegment.Key(key());
    }
    if (match(PathTokenType.LEFT_BRACKET)) {
      return index();
    }

    throw new PathParseError("Unexpected token '" + (String) peek().literal() + "'");
  }

  /**
   * Parses an object key identifier.
   *
   * @return the key string
   * @throws PathParseError if no key is found after '.'
   */
  private String key() {
    PathToken key = consume(PathTokenType.KEY, "Expected key after '.'");
    return (String) key.literal();
  }

  /**
   * Parses an array index access.
   *
   * @return a {@link PathSegment.Index} with the parsed index
   * @throws PathParseError if the index or closing bracket is missing
   */
  private PathSegment.Index index() {
    PathToken index = consume(PathTokenType.INDEX, "Expected an index.");
    consume(PathTokenType.RIGHT_BRACKET, "Expected a ']'");
    return new PathSegment.Index((int) index.literal());
  }
}
