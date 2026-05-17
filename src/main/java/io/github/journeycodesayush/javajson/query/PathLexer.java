package io.github.journeycodesayush.javajson.query;

import java.util.List;

import java.util.ArrayList;

public class PathLexer {
    public enum PathTokenType {
        DOT,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        KEY,
        INDEX,
        EOF
    }

    public record PathToken(PathTokenType pathTokenType, Object literal) {
    }

    private final String source;
    private final List<PathToken> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;

    public PathLexer(String source) {
        this.source = source;
    }

    public List<PathToken> scanTokens() {
        while (!isAtEnd()) {
            scanToken();
            start = current;
        }
        addToken(PathTokenType.EOF);
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

    private void addToken(PathTokenType type) {
        addToken(type, null);
    }

    private void addToken(PathTokenType type, Object literal) {
        tokens.add(new PathToken(type, literal));
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '.' -> {
                addToken(PathTokenType.DOT);
            }
            case '[' -> {
                addToken(PathTokenType.LEFT_BRACKET);
            }
            case ']' -> {
                addToken(PathTokenType.RIGHT_BRACKET);
            }

            default -> {
                if (isDigit(c))
                    index();
                if (isAlpha(c)) {
                    key();

                }
            }
        }
    }

    private void index() {
        while (isDigit(peek()))
            advance();
        addToken(PathTokenType.INDEX, Integer.parseInt(source.substring(start, current)));
    }

    private void key() {
        while (isAlphaNumeric(peek()))
            advance();
        addToken(PathTokenType.KEY, source.substring(start, current));
    }
}
