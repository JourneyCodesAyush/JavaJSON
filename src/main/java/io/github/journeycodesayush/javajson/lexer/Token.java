package io.github.journeycodesayush.javajson.lexer;

public record Token(TokenType type, String lexeme, Object literal, int line) {

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
