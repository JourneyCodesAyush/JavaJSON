package io.github.journeycodesayush.javajson;

import io.github.journeycodesayush.javajson.parser.*;
import io.github.journeycodesayush.javajson.parser.JsonAstPrinter.PrintMode;
import io.github.journeycodesayush.javajson.parser.Parser.ParseError;
import io.github.journeycodesayush.javajson.query.JsonQuery;
import io.github.journeycodesayush.javajson.query.PathLexer;
import io.github.journeycodesayush.javajson.query.PathLexer.PathToken;
import io.github.journeycodesayush.javajson.query.PathParser;
import io.github.journeycodesayush.javajson.query.PathSegment;
import io.github.journeycodesayush.javajson.lexer.*;
import io.github.journeycodesayush.javajson.lexer.Lexer.LexerError;

import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaJSON {

    private static final int EX_USAGE = 64;
    private static final int EX_ERROR = 65;
    private static final int EX_SOFTWARE = 70;

    private static String file(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    private static void printUsage() {
        System.out.println("Usage: javajson <command> <file.json> [args]");
        System.out.println("Commands:");
        System.out.println("  format    Pretty print JSON");
        System.out.println("  minify    Minify JSON");
        System.out.println("  validate  Validate JSON");
        System.out.println("  get       Query JSON with a path expression");
    }

    public static void main(String[] args) {
        // System.out.println("Hello world!");
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
            System.exit(0);
        }

        if (args.length < 2) {
            printUsage();
            System.exit(EX_USAGE);
        }
        String command = args[0];
        String path = args[1];
        try {
            String json = file(path);

            Lexer lexer = new Lexer(json);
            List<Token> tokens = lexer.scanTokens();

            Parser parser = new Parser(tokens);
            JsonValue value = parser.parse();

            switch (command) {
                case "format" -> {
                    System.out.println(new JsonAstPrinter().print(value));
                }

                case "minify" -> {
                    System.out.println(new JsonAstPrinter(PrintMode.MINIFY).print(value));
                }
                case "validate" -> {
                    System.out.println("Valid JSON.");
                    System.exit(0);
                }
                case "get" -> {

                    if (args.length < 3) {
                        System.err.println("Usage: javajson get <file> <path>");
                        return;
                    }

                    String pathQuery = args[2];
                    PathLexer pathLexer = new PathLexer(pathQuery);
                    List<PathToken> pathTokens = pathLexer.scanTokens();

                    PathParser pathParser = new PathParser(pathTokens);
                    List<PathSegment> segments = pathParser.parse();

                    JsonValue result = JsonQuery.get(value, segments);
                    if (!(result instanceof JsonValue.JsonNull)) {
                        System.out.println(new JsonAstPrinter(PrintMode.MINIFY).print(result));
                    }
                }
                default -> {
                    System.err.println("Unknown command: " + command);
                    printUsage();
                    System.exit(EX_USAGE);
                }
            }

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
            System.exit(EX_SOFTWARE);
        } catch (LexerError e) {
            System.err.println(e.getMessage());
            System.exit(EX_ERROR);
        } catch (ParseError e) {
            System.err.println(e.getMessage());
            System.exit(EX_ERROR);
        } catch (Exception e) {
            System.err.println("Internal error: " + e.toString());
            System.exit(EX_SOFTWARE);
        }
    }
}
