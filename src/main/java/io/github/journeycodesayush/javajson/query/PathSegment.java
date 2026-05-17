package io.github.journeycodesayush.javajson.query;

public sealed interface PathSegment permits PathSegment.Key, PathSegment.Index {
    record Key(String name) implements PathSegment {
    }

    record Index(int index) implements PathSegment {
    }
}
