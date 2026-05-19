package io.github.journeycodesayush.javajson;

import io.github.journeycodesayush.javajson.parser.*;
import io.github.journeycodesayush.javajson.parser.JsonAstPrinter.PrintMode;
import io.github.journeycodesayush.javajson.parser.Parser.ParseError;
import io.github.journeycodesayush.javajson.query.JsonQuery;
import io.github.journeycodesayush.javajson.query.PathLexer;
import io.github.journeycodesayush.javajson.query.PathLexer.PathToken;
import io.github.journeycodesayush.javajson.query.PathParser.PathParseError;
import io.github.journeycodesayush.javajson.query.PathParser;
import io.github.journeycodesayush.javajson.query.PathSegment;
import io.github.journeycodesayush.javajson.lexer.*;
import io.github.journeycodesayush.javajson.lexer.Lexer.LexerError;

import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point for the JavaJSON CLI tool.
 * <p>
 * Provides commands to format, minify, validate, and query JSON files
 * using a jq-like path expression syntax.
 * </p>
 */
public class JavaJSON {

    /** Exit code for incorrect usage or missing arguments. */
    private static final int EX_USAGE = 64;

    /** Exit code for lexer or parser errors. */
    private static final int EX_ERROR = 65;

    /** Exit code for internal software errors. */
    private static final int EX_SOFTWARE = 70;

    /**
     * Reads the contents of a file at the given path.
     *
     * @param path the file path to read
     * @return the file contents as a string
     * @throws IOException if the file cannot be read
     */
    private static String file(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    /**
     * Prints usage information and available commands to standard output.
     */
    private static void printUsage() {
        System.out.println("Usage: javajson <command> <file.json> [args]");
        System.out.println("Flags:");
        System.out.println("  -h, --help     Print this");
        System.out.println("  -v, --version  Print version information");
        System.out.println("Commands:");
        System.out.println("  format    Pretty print JSON");
        System.out.println("  minify    Minify JSON");
        System.out.println("  validate  Validate JSON");
        System.out.println("  get       Query JSON with a path expression");
    }

    /**
     * Main entry point for the JavaJSON CLI.
     * <p>
     * Accepts a command and a JSON file path as arguments, with an optional
     * path expression for the {@code get} command.
     * </p>
     *
     * @param args command-line arguments: {@code <command> <file.json> [path]}
     */
    public static void main(String[] args) {
        // System.out.println("Hello world!");
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
            System.exit(0);
        }
        if (args.length == 1 && (args[0].equals("-v") || args[0].equals("--version"))) {
            System.out.println("JavaJSON v0.5.1");
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
        } catch (PathParseError e) {
            System.err.println(e.getMessage());
            System.exit(EX_ERROR);
        } catch (Exception e) {
            System.err.println("Internal error: " + e.toString());
            System.exit(EX_SOFTWARE);
        }
    }
}
