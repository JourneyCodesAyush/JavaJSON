package io.github.journeycodesayush.javajson.parser;

import java.util.List;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import io.github.journeycodesayush.javajson.lexer.Token;
import io.github.journeycodesayush.javajson.lexer.TokenType;
import io.github.journeycodesayush.javajson.parser.JsonValue.*;

import static io.github.journeycodesayush.javajson.lexer.TokenType.*;

/**
 * Recursive descent parser for JSON.
 * <p>
 * Consumes a list of {@link Token} objects produced by the
 * {@link io.github.journeycodesayush.javajson.lexer.Lexer}
 * and produces a {@link JsonValue} tree.
 * </p>
 */
public class Parser {

    /** Thrown when the parser encounters unexpected or invalid tokens. */
    public static class ParseError extends RuntimeException {
        public ParseError(String message) {
            super(message);
        }
    }

    /** The list of tokens to parse. */
    private final List<Token> tokens;

    /** Current position in the token list. */
    private int current = 0;

    /**
     * Checks whether the parser has reached the end of the token stream.
     *
     * @return true if at EOF, false otherwise
     */
    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    /**
     * Returns the current token without consuming it.
     *
     * @return the current {@link Token}
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Returns the most recently consumed token.
     *
     * @return the previous {@link Token}
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Consumes the current token and returns it.
     *
     * @return the consumed {@link Token}
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    /**
     * Checks if the current token is of the given type without consuming it.
     *
     * @param type the {@link TokenType} to check
     * @return true if it matches, false otherwise
     */
    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;

        return peek().type() == type;
    }

    /**
     * Checks if the current token matches any of the given types and advances if
     * so.
     *
     * @param types one or more {@link TokenType} values to match
     * @return true if matched and advanced, false otherwise
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Consumes a token of the expected type or throws a {@link ParseError}.
     *
     * @param type    the expected {@link TokenType}
     * @param message the error message if the type does not match
     * @return the consumed {@link Token}
     * @throws ParseError if the current token does not match the expected type
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(message);
    }

    /**
     * Creates a {@link ParseError} with the given message and current line number.
     *
     * @param message the error description
     * @return a new {@link ParseError}
     */
    private ParseError error(String message) {
        return new ParseError("[PARSER] line " + peek().line() + ": " + message);
    }

    /**
     * Constructs a Parser for the given token list.
     *
     * @param tokens the list of tokens to parse
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the token stream and returns the root {@link JsonValue}.
     *
     * @return the parsed {@link JsonValue}
     */
    public JsonValue parse() {
        JsonValue value = value();
        if (peek().type() != EOF) {
            throw error("Expected EOF but found: " + peek().lexeme());
        }
        return value;
    }

    /**
     * Parses a JSON value — object, array, string, number, boolean, or null.
     *
     * @return the parsed {@link JsonValue}
     * @throws ParseError if no valid value is found
     */
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

    /**
     * Parses a JSON object and returns a {@link JsonValue.JsonObject}.
     *
     * @return the parsed {@link JsonValue.JsonObject}
     * @throws ParseError if the object is malformed
     */
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

    /**
     * Parses a JSON array and returns a {@link JsonValue.JsonArray}.
     *
     * @return the parsed {@link JsonValue.JsonArray}
     * @throws ParseError if the array is malformed
     */
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

    /**
     * Returns a {@link JsonValue.JsonString} from the previously consumed string
     * token.
     *
     * @return the parsed {@link JsonValue.JsonString}
     */
    private JsonString parseJsonString() {

        Token token = previous();

        return new JsonString((String) token.literal());

    }

    /**
     * Returns a {@link JsonValue.JsonNumber} from the previously consumed number
     * token.
     *
     * @return the parsed {@link JsonValue.JsonNumber}
     */
    private JsonNumber parseJsonNumber() {
        Token token = previous();
        return new JsonNumber((Double) (token.literal()));
    }

    /**
     * Returns a {@link JsonValue.JsonBoolean} from the previously consumed boolean
     * token.
     *
     * @return the parsed {@link JsonValue.JsonBoolean}
     */
    private JsonBoolean parseJsonBoolean() {
        Token token = previous();
        if (token.type() == TRUE)
            return new JsonBoolean(true);
        if (token.type() == FALSE)
            return new JsonBoolean(false);

        return new JsonBoolean(false);

    }

    /**
     * Returns a {@link JsonValue.JsonNull} instance.
     *
     * @return a new {@link JsonValue.JsonNull}
     */
    private JsonNull parseJsonNull() {
        return new JsonNull();
    }
}
