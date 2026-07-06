package io.github.journeycodesayush.javajson.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.github.journeycodesayush.javajson.lexer.Lexer;
import io.github.journeycodesayush.javajson.lexer.Token;
import io.github.journeycodesayush.javajson.parser.JsonValue;
import io.github.journeycodesayush.javajson.parser.Parser;
import java.util.List;
import org.junit.jupiter.api.Test;

public class JsonQueryTest {

  private JsonValue query(String json, String path) {
    Lexer lexer = new Lexer(json);
    List<Token> tokens = lexer.scanTokens();
    Parser parser = new Parser(tokens);
    JsonValue root = parser.parse(); // parse json

    PathLexer pathLexer = new PathLexer(path);
    List<PathLexer.PathToken> pathTokens = pathLexer.scanTokens();

    PathParser pathParser = new PathParser(pathTokens);
    List<PathSegment> segments = pathParser.parse(); // parse path

    return JsonQuery.get(root, segments);
  }

  @Test
  void testObjectKeyAccess() {
    JsonValue result = query("{\"name\": \"ayush\"}", ".name");

    assertInstanceOf(JsonValue.JsonString.class, result);
    JsonValue.JsonString str = (JsonValue.JsonString) result;
    assertEquals("ayush", str.value());
  }

  @Test
  void testArrayIndexAccess() {
    JsonValue result = query("[1, 2, 3]", ".[1]");

    assertInstanceOf(JsonValue.JsonNumber.class, result);
    JsonValue.JsonNumber num = (JsonValue.JsonNumber) result;
    assertEquals(2.0, num.value());
  }

  @Test
  void testChainedAccess() {
    JsonValue result = query("{\"users\": [{\"email\": \"a@b.com\"}]}", ".users[0].email");

    assertInstanceOf(JsonValue.JsonString.class, result);
    JsonValue.JsonString str = (JsonValue.JsonString) result;
    assertEquals("a@b.com", str.value());
  }

  @Test
  void testInvalidAccess() {
    JsonValue result = query("{\"name\": \"ayush\"}", ".age"); // key doesn't exist → returns null
    assertInstanceOf(JsonValue.JsonNull.class, result);
  }
}
