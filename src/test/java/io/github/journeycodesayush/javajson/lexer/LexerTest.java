package io.github.journeycodesayush.javajson.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

public class LexerTest {
  @Test
  void testEmptyObject() {
    Lexer lexer = new Lexer("{}");
    List<Token> tokens = lexer.scanTokens();

    // {} EOF
    assertEquals(3, tokens.size());
    assertEquals(TokenType.LEFT_CURLY_BRACE, tokens.get(0).type());
    assertEquals(TokenType.RIGHT_CURLY_BRACE, tokens.get(1).type());
    assertEquals(TokenType.EOF, tokens.get(2).type());
  }

  @Test
  void testEmptyArray() {
    Lexer lexer = new Lexer("[]");
    List<Token> tokens = lexer.scanTokens();

    // [] EOF
    assertEquals(3, tokens.size());
    assertEquals(TokenType.LEFT_BRACKET, tokens.get(0).type());
    assertEquals(TokenType.RIGHT_BRACKET, tokens.get(1).type());
    assertEquals(TokenType.EOF, tokens.get(2).type());
  }

  @Test
  void testSimpleObject() {
    Lexer lexer = new Lexer("{\"a\": \"This is a simple object key-value pair\"}");
    List<Token> tokens = lexer.scanTokens();

    // {"a" : "This is a simple object key-value pair"} EOF
    assertEquals(6, tokens.size());
    assertEquals(TokenType.LEFT_CURLY_BRACE, tokens.get(0).type());

    assertEquals(TokenType.STRING, tokens.get(1).type());
    assertEquals(TokenType.COLON, tokens.get(2).type());
    assertEquals(TokenType.STRING, tokens.get(3).type());
    assertEquals(TokenType.RIGHT_CURLY_BRACE, tokens.get(4).type());
    assertEquals(TokenType.EOF, tokens.get(5).type());
  }

  @Test
  void testSimpleArray() {
    Lexer lexer = new Lexer("[\"a\", \"b\", \"c\"]");
    List<Token> tokens = lexer.scanTokens();

    // ["a", "b", "c"] EOF
    assertEquals(8, tokens.size());
    assertEquals(TokenType.LEFT_BRACKET, tokens.get(0).type());
    assertEquals(TokenType.STRING, tokens.get(1).type());
    assertEquals(TokenType.COMMA, tokens.get(2).type());
    assertEquals(TokenType.STRING, tokens.get(3).type());
    assertEquals(TokenType.COMMA, tokens.get(4).type());
    assertEquals(TokenType.STRING, tokens.get(5).type());
    assertEquals(TokenType.RIGHT_BRACKET, tokens.get(6).type());
    assertEquals(TokenType.EOF, tokens.get(7).type());
  }

  @Test
  void testNumberTypes() {
    Lexer lexer = new Lexer("[1,3.14,1e10]");
    List<Token> tokens = lexer.scanTokens();

    // [1, 3.14, 1e10] EOF
    assertEquals(8, tokens.size());
    assertEquals(TokenType.LEFT_BRACKET, tokens.get(0).type());

    assertEquals(TokenType.NUMBER, tokens.get(1).type());
    assertEquals(1.0, tokens.get(1).literal());

    assertEquals(TokenType.COMMA, tokens.get(2).type());

    assertEquals(TokenType.NUMBER, tokens.get(3).type());
    assertEquals(3.14, tokens.get(3).literal());

    assertEquals(TokenType.COMMA, tokens.get(4).type());

    assertEquals(TokenType.NUMBER, tokens.get(5).type());
    assertEquals(1e10, tokens.get(5).literal());

    assertEquals(TokenType.RIGHT_BRACKET, tokens.get(6).type());
    assertEquals(TokenType.EOF, tokens.get(7).type());
  }

  @Test
  void testBooleanAndNull() {
    Lexer lexer = new Lexer("[true, false, null]");
    List<Token> tokens = lexer.scanTokens();

    // [true, false, null] EOF
    assertEquals(8, tokens.size());
    assertEquals(TokenType.LEFT_BRACKET, tokens.get(0).type());

    assertEquals(TokenType.TRUE, tokens.get(1).type());
    assertEquals(true, tokens.get(1).literal());

    assertEquals(TokenType.COMMA, tokens.get(2).type());

    assertEquals(TokenType.FALSE, tokens.get(3).type());
    assertEquals(false, tokens.get(3).literal());

    assertEquals(TokenType.COMMA, tokens.get(4).type());

    assertEquals(TokenType.NULL, tokens.get(5).type());
    assertEquals(null, tokens.get(5).literal());

    assertEquals(TokenType.RIGHT_BRACKET, tokens.get(6).type());
    assertEquals(TokenType.EOF, tokens.get(7).type());
  }

  @Test
  void testNestedObject() {
    Lexer lexer = new Lexer("{\"a\": {\"b\": \"c\"}}");
    List<Token> tokens = lexer.scanTokens();

    // { "a" : { "b" : "c" } } EOF
    assertEquals(10, tokens.size());
    assertEquals(TokenType.LEFT_CURLY_BRACE, tokens.get(0).type());
    assertEquals(TokenType.STRING, tokens.get(1).type());
    assertEquals("a", tokens.get(1).literal());
    assertEquals(TokenType.COLON, tokens.get(2).type());
    assertEquals(TokenType.LEFT_CURLY_BRACE, tokens.get(3).type());
    assertEquals(TokenType.STRING, tokens.get(4).type());
    assertEquals("b", tokens.get(4).literal());
    assertEquals(TokenType.COLON, tokens.get(5).type());
    assertEquals(TokenType.STRING, tokens.get(6).type());
    assertEquals("c", tokens.get(6).literal());
    assertEquals(TokenType.RIGHT_CURLY_BRACE, tokens.get(7).type());
    assertEquals(TokenType.RIGHT_CURLY_BRACE, tokens.get(8).type());
    assertEquals(TokenType.EOF, tokens.get(9).type());
  }

  @Test
  void testNestedArray() {
    Lexer lexer = new Lexer("[[1, 2], [3, 4]]");
    List<Token> tokens = lexer.scanTokens();

    // [ [ 1 , 2 ] , [ 3 , 4 ] ] EOF
    assertEquals(14, tokens.size());
    assertEquals(TokenType.LEFT_BRACKET, tokens.get(0).type());
    assertEquals(TokenType.LEFT_BRACKET, tokens.get(1).type());
    assertEquals(TokenType.NUMBER, tokens.get(2).type());
    assertEquals(1.0, tokens.get(2).literal());
    assertEquals(TokenType.COMMA, tokens.get(3).type());
    assertEquals(TokenType.NUMBER, tokens.get(4).type());
    assertEquals(2.0, tokens.get(4).literal());
    assertEquals(TokenType.RIGHT_BRACKET, tokens.get(5).type());
    assertEquals(TokenType.COMMA, tokens.get(6).type());
    assertEquals(TokenType.LEFT_BRACKET, tokens.get(7).type());
    assertEquals(TokenType.NUMBER, tokens.get(8).type());
    assertEquals(3.0, tokens.get(8).literal());
    assertEquals(TokenType.COMMA, tokens.get(9).type());
    assertEquals(TokenType.NUMBER, tokens.get(10).type());
    assertEquals(4.0, tokens.get(10).literal());
    assertEquals(TokenType.RIGHT_BRACKET, tokens.get(11).type());
    assertEquals(TokenType.RIGHT_BRACKET, tokens.get(12).type());
    assertEquals(TokenType.EOF, tokens.get(13).type());
  }

  @Test
  void testEscapeSequences() {
    Lexer lexer = new Lexer("{\"quote\": \"He said \\\"hello\\\"\"}");
    List<Token> tokens = lexer.scanTokens();

    // {\"quote\": \"He said \\\"hello\\\"\"}
    assertEquals(TokenType.STRING, tokens.get(3).type());
    assertEquals("He said \"hello\"", tokens.get(3).literal());
  }

  @Test
  void testInvalidIdentifier() {
    // {notakeyword: 1}
    // assert LexerError is thrown
    assertThrows(
        Lexer.LexerError.class,
        () -> {
          new Lexer("{notakeyword : 1}").scanTokens();
        });
  }
}
