package io.github.journeycodesayush.javajson.lexer;

public enum TokenType {
    // Single character tokens
    LEFT_CURLY_BRACE,
    RIGHT_CURLY_BRACE,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    COLON,
    COMMA,

    // Unary Operator
    MINUS,

    // String, boolean, number
    STRING,
    TRUE,
    FALSE,
    NUMBER,
    NULL,

    // End of File
    EOF
}
