package io.github.journeycodesayush.javajson.query;

import java.util.List;

import java.util.ArrayList;

/**
 * Lexer for jq-like JSON path expressions.
 * <p>
 * Converts a path string such as {@code .users[0].email} into a list
 * of {@link PathToken} objects for consumption by the {@link PathParser}.
 * </p>
 */
public class PathLexer {
    /** Token types recognized in a JSON path expression. */
    public enum PathTokenType {
        DOT,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        KEY,
        INDEX,
        EOF
    }

    /**
     * Represents a single token in a path expression.
     *
     * @param pathTokenType the type of this token
     * @param literal       the literal value, or {@code null} if not applicable
     */
    public record PathToken(PathTokenType pathTokenType, Object literal) {
    }

    /** The path expression string to tokenize. */
    private final String source;

    /** The list of tokens produced after scanning. */
    private final List<PathToken> tokens = new ArrayList<>();

    /** Start index of the current token in the source string. */
    private int start = 0;

    /** Current index being processed in the source string. */
    private int current = 0;

    /**
     * Constructs a PathLexer for the given path expression.
     *
     * @param source the path expression string to tokenize
     */
    public PathLexer(String source) {
        this.source = source;
    }

    /**
     * Scans the entire path expression and returns a list of tokens.
     *
     * @return a list of {@link PathToken} objects
     */
    public List<PathToken> scanTokens() {
        while (!isAtEnd()) {
            scanToken();
            start = current;
        }
        addToken(PathTokenType.EOF);
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
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
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
     * Adds a token of the given type with no literal value.
     *
     * @param type the {@link PathTokenType} of the token
     */
    private void addToken(PathTokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token of the given type with a literal value.
     *
     * @param type    the {@link PathTokenType} of the token
     * @param literal the literal value, or {@code null} if not applicable
     */
    private void addToken(PathTokenType type, Object literal) {
        tokens.add(new PathToken(type, literal));
    }

    /**
     * Scans a single token from the source and adds it to the token list.
     */
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

    /**
     * Scans an integer index and adds it as an {@link PathTokenType#INDEX} token.
     */
    private void index() {
        while (isDigit(peek()))
            advance();
        addToken(PathTokenType.INDEX, Integer.parseInt(source.substring(start, current)));
    }

    /**
     * Scans an alphanumeric key and adds it as a {@link PathTokenType#KEY} token.
     */
    private void key() {
        while (isAlphaNumeric(peek()))
            advance();
        addToken(PathTokenType.KEY, source.substring(start, current));
    }
}
