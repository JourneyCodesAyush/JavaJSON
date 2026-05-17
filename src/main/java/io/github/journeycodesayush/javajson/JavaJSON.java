package io.github.journeycodesayush.javajson;

import io.github.journeycodesayush.javajson.parser.*;
import io.github.journeycodesayush.javajson.parser.Parser.ParseError;
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

    public static void main(String[] args) {
        // System.out.println("Hello world!");
        if (args.length == 0) {
            System.err.println("usage: javajson <input>.json");
            System.exit(EX_USAGE);
        }
        try {
            String json = file(args[0]);

            Lexer lexer = new Lexer(json);
            List<Token> tokens = lexer.scanTokens();
            // for (Token token : tokens) {
            // System.out.println(token.toString());
            // }

            System.out.println("Original JSON:\n" + json + "\n");
            Parser parser = new Parser(tokens);
            JsonValue value = parser.parse();

            System.out.println(new JsonAstPrinter().print(value));

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
            System.exit(EX_SOFTWARE);
        } catch (LexerError e) {
            System.err.println("Lexer error: " + e.getMessage());
            System.exit(EX_ERROR);
        } catch (ParseError e) {
            System.err.println("Parser error: " + e.getMessage());
            System.exit(EX_ERROR);
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Internal error: " + e.toString());
            System.exit(EX_SOFTWARE);
        }
    }
}
