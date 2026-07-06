package io.github.journeycodesayush.javajson.query;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

public class PathParserTest {

  private List<PathSegment> parse(String path) {
    PathLexer lexer = new PathLexer(path);
    List<PathLexer.PathToken> tokens = lexer.scanTokens();
    return new PathParser(tokens).parse();
  }

  @Test
  void testEmptyKey() {
    List<PathSegment> path = parse("");
    assertEquals(0, path.size());
  }

  @Test
  void testArrayIndex() {
    List<PathSegment> path = parse(".[0]");
    assertEquals(1, path.size());

    assertInstanceOf(PathSegment.Index.class, path.get(0));
    PathSegment.Index index = (PathSegment.Index) path.get(0);
    assertEquals(0, index.index());
  }

  @Test
  void testChainedPath() {
    List<PathSegment> path = parse(".users[0].email");
    assertEquals(3, path.size());

    assertInstanceOf(PathSegment.Key.class, path.get(0));
    PathSegment.Key key1 = (PathSegment.Key) path.get(0);
    assertEquals("users", key1.name());

    assertInstanceOf(PathSegment.Index.class, path.get(1));
    PathSegment.Index index = (PathSegment.Index) path.get(1);
    assertEquals(0, index.index());

    assertInstanceOf(PathSegment.Key.class, path.get(2));
    PathSegment.Key key2 = (PathSegment.Key) path.get(2);
    assertEquals("email", key2.name());
  }
}
