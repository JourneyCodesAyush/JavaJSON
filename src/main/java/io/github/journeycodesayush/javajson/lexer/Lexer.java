package io.github.journeycodesayush.javajson.lexer;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import static io.github.journeycodesayush.javajson.lexer.TokenType.*;

public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("null", NULL);

    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text.trim(), literal, line));
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '{' -> addToken(LEFT_CURLY_BRACE);
            case '}' -> addToken(RIGHT_CURLY_BRACE);
            case '[' -> addToken(LEFT_BRACKET);
            case ']' -> addToken(RIGHT_BRACKET);

            case ':' -> addToken(COLON);
            case ',' -> addToken(COMMA);

            case ' ', '\t', '\r' -> {

            }
            case '\n' -> {
                line++;
            }

            case '"' -> {
                string('"');
            }
            default -> {
                if (isDigit(c))
                    number();
                else if (isAlphaNumeric(c)) {
                    identifier();
                }
            }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);

        if (type == null)
            return;
        addToken(type);
    }

    private void string(char quote) {
        while (peek() != quote && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }
        if (isAtEnd()) {
            System.out.println("Unterminated string.");
            return;
        }
        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        // Integer
        while (isDigit(peek()))
            advance();
        // Decimal part
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek()))
                advance();
        }

        // Exponent part
        if (peek() == 'e' || peek() == 'E') {
            advance();
            if (peek() == '+' || peek() == '-') {
                advance();
            }
            if (!isDigit(peek())) {
                System.err.println("Not a valid exponent.");
                return;
            }

            while (isDigit(peek())) {
                advance();
            }
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

}
