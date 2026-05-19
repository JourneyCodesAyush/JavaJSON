package io.github.journeycodesayush.javajson.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.journeycodesayush.javajson.lexer.Lexer;
import io.github.journeycodesayush.javajson.lexer.Token;

public class ParserTest {
    private JsonValue parse(String json) {
        Lexer lexer = new Lexer(json);
        List<Token> tokens = lexer.scanTokens();
        return new Parser(tokens).parse();
    }

    @Test
    void testEmptyObject() {
        // {} EOF
        JsonValue result = parse("{}");
        assertInstanceOf(JsonValue.JsonObject.class, result);
        JsonValue.JsonObject obj = (JsonValue.JsonObject) result;
        assertEquals(0, obj.members().size());
    }

    @Test
    void testEmptyArray() {
        // [] EOF
        JsonValue result = parse("[]");

        assertInstanceOf(JsonValue.JsonArray.class, result);

        JsonValue.JsonArray arr = (JsonValue.JsonArray) result;
        assertEquals(0, arr.elements().size());
    }

    @Test
    void testSimpleObject() {

        // {"a" : "This is a simple object key-value pair"} EOF
        JsonValue result = parse("{\"a\": \"This is a simple object key-value pair\"}");

        assertInstanceOf(JsonValue.JsonObject.class, result);

        JsonValue.JsonObject obj = (JsonValue.JsonObject) result;
        assertEquals(1, obj.members().size());

        JsonValue.JsonString value = (JsonValue.JsonString) obj.members().get("a");
        assertEquals("This is a simple object key-value pair", value.value());
    }

    @Test
    void testSimpleArray() {

        // ["a", "b", "c"] EOF
        JsonValue result = parse("[\"a\", \"b\", \"c\"]");

        assertInstanceOf(JsonValue.JsonArray.class, result);

        JsonValue.JsonArray arr = (JsonValue.JsonArray) result;
        assertEquals(3, arr.elements().size());

        assertEquals("a", ((JsonValue.JsonString) arr.elements().get(0)).value());
        assertEquals("b", ((JsonValue.JsonString) arr.elements().get(1)).value());
        assertEquals("c", ((JsonValue.JsonString) arr.elements().get(2)).value());
    }

    @Test
    void testNumberParsing() {

        // [1, 3.14, 1e10] EOF
        JsonValue result = parse("[1, 3.14, 1e10]");

        assertInstanceOf(JsonValue.JsonArray.class, result);

        JsonValue.JsonArray arr = (JsonValue.JsonArray) result;
        assertEquals(3, arr.elements().size());

        assertEquals(1.0, ((JsonValue.JsonNumber) arr.elements().get(0)).value());
        assertEquals(3.14, ((JsonValue.JsonNumber) arr.elements().get(1)).value());
        assertEquals(1e10, ((JsonValue.JsonNumber) arr.elements().get(2)).value());
    }

    @Test
    void testNegativeNumber() {
        JsonValue result = parse("[-1, -3.14, -1e10]");

        assertInstanceOf(JsonValue.JsonArray.class, result);

        JsonValue.JsonArray arr = (JsonValue.JsonArray) result;
        assertEquals(3, arr.elements().size());

        assertEquals(-1.0, ((JsonValue.JsonNumber) arr.elements().get(0)).value());
        assertEquals(-3.14, ((JsonValue.JsonNumber) arr.elements().get(1)).value());
        assertEquals(-1e10, ((JsonValue.JsonNumber) arr.elements().get(2)).value());
    }

    @Test
    void testBooleanAndNull() {

        // [true, false, null] EOF
        JsonValue result = parse("[true, false, null]");

        assertInstanceOf(JsonValue.JsonArray.class, result);

        JsonValue.JsonArray arr = (JsonValue.JsonArray) result;
        assertEquals(3, arr.elements().size());

        assertEquals(true, ((JsonValue.JsonBoolean) arr.elements().get(0)).value());
        assertEquals(false, ((JsonValue.JsonBoolean) arr.elements().get(1)).value());
        assertInstanceOf(JsonValue.JsonNull.class, arr.elements().get(2));
    }

    @Test
    void testNestedObject() {

        // { "a" : { "b" : "c" } } EOF
        JsonValue result = parse("{\"a\": {\"b\": \"c\"}}");
        // cast to JsonObject
        // get "a" from members — it's another JsonObject
        // get "b" from inner members — it's a JsonString
        // assert value is "c"

        assertInstanceOf(JsonValue.JsonObject.class, result);

        JsonValue.JsonObject obj = (JsonValue.JsonObject) result;
        assertEquals(1, obj.members().size());

        // get inner object by key "a"
        JsonValue.JsonObject inner = (JsonValue.JsonObject) obj.members().get("a");
        assertEquals(1, inner.members().size());

        // get string value by key "b"
        JsonValue.JsonString value = (JsonValue.JsonString) inner.members().get("b");
        assertEquals("c", value.value());
    }

    @Test
    void testNestedArray() {
        // [ [ 1 , 2 ] , [ 3 , 4 ] ]
        JsonValue result = parse("[ [ 1 , 2 ] , [ 3 , 4 ] ]");

        assertInstanceOf(JsonValue.JsonArray.class, result);

        JsonValue.JsonArray arr = (JsonValue.JsonArray) result;
        assertEquals(2, arr.elements().size());

        JsonValue.JsonArray firstArray = (JsonValue.JsonArray) arr.elements().get(0);
        assertEquals(2, firstArray.elements().size());

        assertEquals(1.0, ((JsonValue.JsonNumber) firstArray.elements().get(0)).value());
        assertEquals(2.0, ((JsonValue.JsonNumber) firstArray.elements().get(1)).value());

        JsonValue.JsonArray secondArray = (JsonValue.JsonArray) arr.elements().get(1);
        assertEquals(2, secondArray.elements().size());
        assertEquals(3.0, ((JsonValue.JsonNumber) secondArray.elements().get(0)).value());
        assertEquals(4.0, ((JsonValue.JsonNumber) secondArray.elements().get(1)).value());
    }

    @Test
    void testInvalidJson() {
        // Trailing comma NOT allowed
        assertThrows(Parser.ParseError.class, () -> {
            parse("{\"a\": 1,}");
        });
    }
}
