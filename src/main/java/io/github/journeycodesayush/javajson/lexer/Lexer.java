package io.github.journeycodesayush.javajson.lexer;

import static io.github.journeycodesayush.javajson.lexer.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lexer for JSON source strings.
 *
 * <p>Converts a raw JSON string into a list of {@link Token} objects for consumption by the {@link
 * io.github.journeycodesayush.javajson.parser.Parser}.
 */
public class Lexer {

  /** Thrown when the lexer encounters invalid or unexpected input. */
  public static class LexerError extends RuntimeException {
    public LexerError(String message) {
      super(message);
    }
  }

  /** The JSON source string to tokenize. */
  private final String source;

  /** The list of tokens produced after scanning. */
  private final List<Token> tokens = new ArrayList<>();

  /** Start index of the current lexeme in the source string. */
  private int start = 0;

  /** Current index being processed in the source string. */
  private int current = 0;

  /** Current line number in the source, used for error reporting. */
  private int line = 1;

  /** Mapping of JSON literal keywords to their token types. */
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("true", TRUE);
    keywords.put("false", FALSE);
    keywords.put("null", NULL);
  }

  /**
   * Constructs a Lexer for the given JSON source string.
   *
   * @param source the JSON string to tokenize
   */
  public Lexer(String source) {
    this.source = source;
  }

  /**
   * Scans the entire source string and returns a list of tokens.
   *
   * @return a list of {@link Token} objects representing the scanned source
   */
  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  /**
   * Checks whether the lexer has reached the end of the source string.
   *
   * @return true if all characters have been processed, false otherwise
   */
  private boolean isAtEnd() {
    return current >= source.length();
  }

  /**
   * Returns the current character without advancing the lexer.
   *
   * @return the current character, or {@code '\0'} if at end
   */
  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  /**
   * Returns the next character without advancing the lexer.
   *
   * @return the next character, or {@code '\0'} if at end
   */
  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  /**
   * Adds a token of the given type with no literal value.
   *
   * @param type the {@link TokenType} of the token
   */
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  /**
   * Adds a token of the given type with a literal value.
   *
   * @param type the {@link TokenType} of the token
   * @param literal the literal value, or {@code null} if not applicable
   */
  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text.trim(), literal, line));
  }

  /** Scans a single token from the source and adds it to the token list. */
  private void scanToken() {
    char c = advance();

    switch (c) {
      case '{' -> addToken(LEFT_CURLY_BRACE);
      case '}' -> addToken(RIGHT_CURLY_BRACE);
      case '[' -> addToken(LEFT_BRACKET);
      case ']' -> addToken(RIGHT_BRACKET);

      case ':' -> addToken(COLON);
      case ',' -> addToken(COMMA);
      case '-' -> {
        if (isDigit(peek())) {
          addToken(MINUS);
        } else {
          throw error("Expected digit after '-'");
        }
      }

      case ' ', '\t', '\r' -> {}

      case '\n' -> {
        line++;
      }

      case '"' -> {
        string('"');
      }
      default -> {
        if (isDigit(c)) number();
        else if (isAlphaNumeric(c)) {
          literalKeyword();
        } else {
          throw error("Unexpected character '" + c + "'");
        }
      }
    }
  }

  /**
   * Scans a keyword literal ({@code true}, {@code false}, {@code null}) and adds the corresponding
   * token.
   *
   * @throws LexerError if the identifier is not a valid JSON keyword
   */
  private void literalKeyword() {
    while (isAlphaNumeric(peek())) advance();

    String text = source.substring(start, current);
    TokenType type = keywords.get(text);

    if (type == null) throw error("Unidentified identifier '" + text + "'");
    Object literal =
        switch (type) {
          case TRUE -> true;
          case FALSE -> false;
          case NULL -> null;
          default -> null;
        };
    addToken(type, literal);
  }

  /**
   * Scans a string literal, handling escape sequences, and adds it as a token.
   *
   * @param quote the closing quote character
   * @throws LexerError if the string is unterminated or contains an invalid escape sequence
   */
  private void string(char quote) {
    StringBuilder value = new StringBuilder();

    while (!isAtEnd()) {
      char c = advance();

      if (c == quote) {
        addToken(STRING, value.toString());
        return;
      }

      if (c == '\\') {
        if (isAtEnd()) {
          throw error("Unterminated escape sequence");
        }

        char next = advance();

        switch (next) {
          case '"':
            value.append('"');
            break;
          case '\\':
            value.append('\\');
            break;
          case '/':
            value.append('/');
            break;
          case 'n':
            value.append('\n');
            break;
          case 'r':
            value.append('\r');
            break;
          case 't':
            value.append('\t');
            break;
          case 'b':
            value.append('\b');
            break;
          case 'f':
            value.append('\f');
            break;

          case 'u':
            // Unicode escape \u0000
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4; i++) {
              if (isAtEnd()) {
                throw error("Incomplete unicode escape");
              }
              hex.append(advance());
            }
            value.append((char) Integer.parseInt(hex.toString(), 16));
            break;

          default:
            throw error("Invalid escape sequence \\" + next);
        }

      } else {
        if (c < 0x20) throw error("Unescaped control character in string");

        if (c == '\n') line++;
        value.append(c);
      }
    }

    throw error("Unterminated string");
  }

  /**
   * Creates and throws a {@link LexerError} with the given message and current line number.
   *
   * @param message the error description
   * @throws LexerError always
   */
  private LexerError error(String message) {
    throw new LexerError("[LEXER] line " + line + ": " + message);
  }

  /**
   * Advances the lexer by one character and returns it.
   *
   * @return the consumed character
   */
  private char advance() {
    return source.charAt(current++);
  }

  /**
   * Checks if a character is alphabetic (a-z, A-Z, or underscore).
   *
   * @param c the character to check
   * @return true if alphabetic, false otherwise
   */
  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  /**
   * Checks if a character is alphanumeric.
   *
   * @param c the character to check
   * @return true if alphanumeric, false otherwise
   */
  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  /**
   * Checks if a character is a digit (0-9).
   *
   * @param c the character to check
   * @return true if a digit, false otherwise
   */
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  /**
   * Scans a number literal (integer, decimal, or scientific notation) and adds it as a {@link
   * TokenType#NUMBER} token.
   *
   * @throws LexerError if the exponent part is malformed
   */
  private void number() {
    // Leading zero's not allowed
    if (source.charAt(start) == '0' && isDigit(peek())) {
      throw error("Leading zeroes are not allowed");
    }

    // Integer
    while (isDigit(peek())) advance();
    // Decimal part
    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      while (isDigit(peek())) advance();
    }

    // Exponent part
    if (peek() == 'e' || peek() == 'E') {
      advance();
      if (peek() == '+' || peek() == '-') {
        advance();
      }
      if (!isDigit(peek())) {
        throw error("Not a valid exponent");
      }

      while (isDigit(peek())) {
        advance();
      }
    }
    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }
}
