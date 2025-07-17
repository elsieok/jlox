package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class jlox{

    private static final Interpreter interpreter = new Interpreter();
    
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64); 
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt(); // this is the REPL
        }
  }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
        
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) { 
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            if (line.trim().isEmpty()) continue; // skip empty lines

            boolean containsKeyword = false;
            for (String keyword : Scanner.getKeywords().keySet()) {
                if (line.contains(keyword)) {
                    containsKeyword = true;
                    break;
                }
            }

            if (line.endsWith(";") || containsKeyword || line.contains("{") || line.contains("}")) {
                run(line);
            } else {
                runExpression(line); // Challenge 8.1
            }

            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // stop if there was a syntax error
        if (hadError) return;

        interpreter.interpret(statements);

        // // Print the AST for debugging purposes
        // System.out.println(new ASTPrinter().print(expression));

        // // For now, just print the tokens.
        // for (Token token : tokens) {
        // System.out.println(token);
        // }
    }

    // Challenge 8.1
    private static void runExpression(String line) {
        Scanner scanner = new Scanner(line);
        List<Token> tokens = scanner.scanTokens();

        Parser exprParser = new Parser(tokens);
        Expr expr = exprParser.parseExpression();
        if (!hadError && expr != null) {
            System.out.println("had error " + hadError);
            Object result = interpreter.interpretExpression(expr);
            if (result != null) {
                System.out.println(result);
            }
            return;
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
    
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

}
