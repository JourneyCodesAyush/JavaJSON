package io.github.journeycodesayush.javajson.lexer;

/**
 * Represents a single token produced by the {@link Lexer}.
 * <p>
 * Each token captures the token type, the raw source text (lexeme),
 * the evaluated literal value (if any), and the line number where
 * the token appears in the source.
 * </p>
 *
 * @param type    the {@link TokenType} of this token
 * @param lexeme  the raw source text of this token
 * @param literal the evaluated literal value, or {@code null} if not applicable
 * @param line    the line number in the source where this token appears
 */
public record Token(TokenType type, String lexeme, Object literal, int line) {

    /**
     * Returns a formatted string representation of this token,
     * showing the type, lexeme, literal, and line number.
     *
     * @return a formatted string representation of this token
     */
    @Override
    public String toString() {
        String displayLexeme = (lexeme == null || lexeme.isEmpty()) ? "<none>" : lexeme;
        String displayLiteral = (literal == null) ? "<null>" : literal.toString();
        return String.format(
                "Type: %-15s Lexeme: %-20s Literal: %-15s Line: %d",
                type.toString(),
                displayLexeme,
                displayLiteral,
                line);
    }
}