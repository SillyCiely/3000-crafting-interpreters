package syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class Lox {
    static boolean hadError = false;
    static String command;

    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            System.out.println("Usage: syntax.Lox [command: scan / parse] [script]");
            System.exit(64);
        } else if (args.length == 2){
            command = args[0];
            runFile(args[1]);
            System.exit(0);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            hadError = false;
            run(line);
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        if (command == null) {
            System.out.println("scanner output:");
            for (Token token : tokens) {
                System.out.println(token);
            }

            System.out.println();

            Parser parser = new Parser(tokens);
            Expr expression = parser.parse();

            AstPrinter printer = new AstPrinter();
            System.out.println("parser output");
            System.out.println(printer.print(expression));
        }

        else {
            if (Objects.equals(command, "scan")) {
//            System.out.println("scanner output:");
                for (Token token : tokens) {
                    System.out.println(token);
                }
            }

            Parser parser = new Parser(tokens);
            Expr expression = parser.parse();

            AstPrinter printer = new AstPrinter();
            if (Objects.equals(command, "parse")) {
//            System.out.println("parser output");
                System.out.println(printer.print(expression));
            }
        }

    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}