package io.github.journeycodesayush.javajson.lexer;

/**
 * Represents all token types recognized by the {@link Lexer}.
 * <p>
 * Tokens are categorized into structural characters, operators,
 * literals, and control tokens.
 * </p>
 */
public enum TokenType {

    // Single character tokens
    /** Opening curly brace {@code {}. */
    LEFT_CURLY_BRACE,
    /** Closing curly brace {@code }}. */
    RIGHT_CURLY_BRACE,
    /** Opening square bracket {@code [}. */
    LEFT_BRACKET,
    /** Closing square bracket {@code ]}. */
    RIGHT_BRACKET,
    /** Colon {@code :} separating keys and values. */
    COLON,
    /** Comma {@code ,} separating elements. */
    COMMA,

    // Unary Operator
    /** Minus sign {@code -} for negative numbers. */
    MINUS,

    // Literals
    /** A double-quoted string value. */
    STRING,
    /** The literal {@code true}. */
    TRUE,
    /** The literal {@code false}. */
    FALSE,
    /** A numeric value (integer, decimal, or scientific notation). */
    NUMBER,
    /** The literal {@code null}. */
    NULL,

    // End of File
    /** Marks the end of the token stream. */
    EOF
}