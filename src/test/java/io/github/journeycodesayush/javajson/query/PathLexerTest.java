package io.github.journeycodesayush.javajson.query;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PathLexerTest {

    private List<PathLexer.PathToken> lex(String path) {
        return new PathLexer(path).scanTokens();
    }

    @Test
    void testEmptyKey() {
        List<PathLexer.PathToken> tokens = lex("");
        assertEquals(1, tokens.size());
        assertEquals(PathLexer.PathTokenType.EOF, tokens.get(0).pathTokenType());
    }

    @Test
    void testSimpleKey() {
        List<PathLexer.PathToken> tokens = lex(".name");

        assertEquals(3, tokens.size());
        assertEquals(PathLexer.PathTokenType.DOT, tokens.get(0).pathTokenType());
        assertEquals(PathLexer.PathTokenType.KEY, tokens.get(1).pathTokenType());
        assertEquals("name", tokens.get(1).literal());
        assertEquals(PathLexer.PathTokenType.EOF, tokens.get(2).pathTokenType());
    }

    @Test
    void testArrayIndex() {
        List<PathLexer.PathToken> tokens = lex(".[0]");

        assertEquals(5, tokens.size());
        assertEquals(PathLexer.PathTokenType.DOT, tokens.get(0).pathTokenType());
        assertEquals(PathLexer.PathTokenType.LEFT_BRACKET, tokens.get(1).pathTokenType());
        assertEquals(PathLexer.PathTokenType.INDEX, tokens.get(2).pathTokenType());
        assertEquals(0, tokens.get(2).literal());
        assertEquals(PathLexer.PathTokenType.RIGHT_BRACKET, tokens.get(3).pathTokenType());
        assertEquals(PathLexer.PathTokenType.EOF, tokens.get(4).pathTokenType());
    }

    @Test
    void testChainedPath() {
        List<PathLexer.PathToken> tokens = lex(".users[0].email");

        assertEquals(8, tokens.size());
        assertEquals(PathLexer.PathTokenType.DOT, tokens.get(0).pathTokenType());
        assertEquals(PathLexer.PathTokenType.KEY, tokens.get(1).pathTokenType());
        assertEquals("users", tokens.get(1).literal());

        assertEquals(PathLexer.PathTokenType.LEFT_BRACKET, tokens.get(2).pathTokenType());
        assertEquals(PathLexer.PathTokenType.INDEX, tokens.get(3).pathTokenType());
        assertEquals(0, tokens.get(3).literal());
        assertEquals(PathLexer.PathTokenType.RIGHT_BRACKET, tokens.get(4).pathTokenType());
        assertEquals(PathLexer.PathTokenType.DOT, tokens.get(5).pathTokenType());
        assertEquals(PathLexer.PathTokenType.KEY, tokens.get(6).pathTokenType());
        assertEquals("email", tokens.get(6).literal());
        assertEquals(PathLexer.PathTokenType.EOF, tokens.get(7).pathTokenType());
    }
}