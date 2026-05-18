package io.github.journeycodesayush.javajson.parser;

import java.util.List;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import io.github.journeycodesayush.javajson.lexer.Token;
import io.github.journeycodesayush.javajson.lexer.TokenType;
import io.github.journeycodesayush.javajson.parser.JsonValue.*;

import static io.github.journeycodesayush.javajson.lexer.TokenType.*;

public class Parser {

    public static class ParseError extends RuntimeException {
        public ParseError(String message) {
            super(message);
        }
    }

    private final List<Token> tokens;
    private int current = 0;

    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;

        return peek().type() == type;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(message);
    }

    private ParseError error(String message) {
        return new ParseError("[PARSER] line " + peek().line() + ": " + message);
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public JsonValue parse() {
        try {
            JsonValue value = value();
            // consume(EOF, "Expected end of file.");
            if (peek().type() != EOF) {
                throw error("Expected EOF but found: " + peek().lexeme());
            }
            return value;
        } catch (ParseError e) {
            // TODO: handle exception
            throw e;
        }

    }

    private JsonValue value() {
        if (match(LEFT_CURLY_BRACE))
            return parseJsonObject();
        if (match(LEFT_BRACKET))
            return parseJsonArray();

        if (match(STRING))
            return parseJsonString();

        if (match(MINUS)) {
            Token token = consume(NUMBER, "Expected a number after '-'");
            return new JsonValue.JsonNumber(-((Double) token.literal()));
        }
        if (match(NUMBER))
            return parseJsonNumber();

        if (match(TRUE))
            return parseJsonBoolean();

        if (match(FALSE))
            return parseJsonBoolean();

        if (match(NULL))
            return parseJsonNull();

        throw error("Unexpected token: " + peek().lexeme());
    }

    private JsonObject parseJsonObject() {
        Map<String, JsonValue> members = new LinkedHashMap<>();

        if (match(RIGHT_CURLY_BRACE))
            return new JsonObject(members);

        do {

            Token token = consume(STRING, "Expected a string.");
            String key = (String) token.literal();
            consume(COLON, "Expected a ':'");

            JsonValue value = value();
            members.put(key, value);
        } while (match(COMMA));
        consume(RIGHT_CURLY_BRACE, "Expected a '}'");

        return new JsonObject(members);
    }

    private JsonArray parseJsonArray() {
        List<JsonValue> elements = new ArrayList<>();

        if (match(RIGHT_BRACKET))
            return new JsonArray(elements);

        do {
            JsonValue value = value();
            elements.add(value);

        } while (match(COMMA));

        consume(RIGHT_BRACKET, "Expected a ']'");

        return new JsonArray(elements);
    }

    private JsonString parseJsonString() {

        Token token = previous();

        return new JsonString((String) token.literal());

    }

    private JsonNumber parseJsonNumber() {
        Token token = previous();
        return new JsonNumber((Double) (token.literal()));
    }

    private JsonBoolean parseJsonBoolean() {
        Token token = previous();
        if (token.type() == TRUE)
            return new JsonBoolean(true);
        if (token.type() == FALSE)
            return new JsonBoolean(false);

        return new JsonBoolean(false);

    }

    private JsonNull parseJsonNull() {
        return new JsonNull();
    }
}
