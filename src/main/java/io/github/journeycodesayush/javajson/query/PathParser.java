package io.github.journeycodesayush.javajson.query;

import java.util.ArrayList;
import java.util.List;

import io.github.journeycodesayush.javajson.query.PathLexer.PathToken;
import io.github.journeycodesayush.javajson.query.PathLexer.PathTokenType;

/**
 * Grammar
 * path → segment+
 * // segment → "." key // object access → .name
 * | "[" index "]" // array access → [0]
 * key → identifier
 * index → integer
 */
public class PathParser {
    private final List<PathToken> tokens;

    private int current = 0;

    public PathParser(List<PathToken> tokens) {
        this.tokens = tokens;
    }

    private boolean isAtEnd() {
        return peek().pathTokenType() == PathTokenType.EOF;
    }

    private PathToken peek() {
        return tokens.get(current);
    }

    private PathToken previous() {
        return tokens.get(current - 1);
    }

    private PathToken advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean check(PathTokenType type) {
        if (isAtEnd())
            return false;

        return peek().pathTokenType() == type;
    }

    private boolean match(PathTokenType... types) {
        for (PathTokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private PathToken consume(PathTokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw new RuntimeException(message);
    }

    public List<PathSegment> parse() {
        try {
            List<PathSegment> pathSegments = path();
            return pathSegments;
        } catch (Exception e) {
            // TODO: handle exception
            throw e;
        }
    }

    private List<PathSegment> path() {
        List<PathSegment> segments = new ArrayList<>();
        while (!isAtEnd()) {
            segments.add(segment());
        }
        return segments;
    }

    private PathSegment segment() {
        if (match(PathTokenType.DOT)) {
            if (match(PathTokenType.LEFT_BRACKET)) {
                return index();
            }
            return new PathSegment.Key(key());
        }
        if (match(PathTokenType.LEFT_BRACKET)) {
            return index();
        }

        System.err.println("Unexpected token: " + (String) peek().literal());
        return null;
    }

    private String key() {
        PathToken key = consume(PathTokenType.KEY, "Expected key after '.'");
        return (String) key.literal();
    }

    private PathSegment.Index index() {
        PathToken index = consume(PathTokenType.INDEX, "Expected an index.");
        consume(PathTokenType.RIGHT_BRACKET, "Expected a ']'");
        return new PathSegment.Index((int) index.literal());
    }

}
