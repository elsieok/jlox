package jlox;

import java.util.ArrayList;
import java.util.List;
import static jlox.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    // Challenge 8.1: Parse an expression
    Expr parseExpression() {
    try {
        return expression(); // Start parsing with the `expression()` rule
    } catch (ParseError error) {
        return null;
    }
}

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronise();
            return null; // or a dummy statement if needed
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        
        Expr initialiser = null;
        if (match(EQUAL)) {
            initialiser = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initialiser);
    }

    private Stmt statement() {
        if (match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
        statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr expression(){
        return comma();
    }

    private Expr comma() {
        if (match(COMMA)) {
            Token operator = previous();
            error(operator, "Missing left-hand operand before " + operator.lexeme);
            conditional(); // discard right-hand side
            return null; // or a dummy expression if needed
        }

        Expr expr = conditional();

        while (match(COMMA)) {
            Token operator = previous();
            Expr right = conditional();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr conditional() {
        Expr expr = assignment();

        if (match(QUESTION)) {
            Token op1 = previous();
            Expr mid = expression();

            // Throw error if ':' is missing
            Token op2 = consume(COLON, "Expect ':' after '?' expression.");
            Expr right = conditional();
            expr = new Expr.Ternary(expr, op1, mid, op2, right);
            
        }

        return expr;
    }

    private Expr assignment() {
        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr equality() {
        if (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            error(operator, "Missing left-hand operand before " + operator.lexeme);
            comparison(); // discard right-hand side
            return null; // or a dummy expression if needed
        }

        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        if (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
            Token operator = previous();
            error(operator, "Missing left-hand operand before " + operator.lexeme);
            term(); // discard right-hand side
            return null; // or a dummy expression if needed
        }

        Expr expr = term();

        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        if (match(MINUS, PLUS)) {
            Token operator = previous();
            error(operator, "Missing left-hand operand before " + operator.lexeme);
            factor(); // discard right-hand side
            return null; // or a dummy expression if needed
        }

        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        if (match(SLASH, STAR)) {
            Token operator = previous();
            error(operator, "Missing left-hand operand before " + operator.lexeme);
            unary(); // discard right-hand side
            return null; // or a dummy expression if needed
        }

        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(TRUE)) return new Expr.Literal(true);
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after exression.");
            return new Expr.Grouping(expr);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        jlox.error(token, message);
        return new ParseError();
    }

    @SuppressWarnings({"incomplete-switch"})
    private void synchronise() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }

            advance();
        }
    }


    /* GRAMMAR
     * program        → statement* EOF
     * declaration    → varDecl | statement
     * varDecl        → "var" IDENTIFIER ( "=" expression )? ";"
     * statement      → exprStmt | printStmt | block
     * printStmt      → "print" expression ";"
     * exprStmt       → expression ";"
     * block          → "{" declaration* "}"
     * 
     * expression     → comma
     * comma          → conditional ( "," conditional )*        // Challenge 6.1
     * conditional    → assignment ( "?" expression ":" conditional )?      // Challenge 6.1
     * assignment     → IDENTIFIER "=" assignment | equality
     * equality       → comparison ( ( "!=" | "==" ) comparison )*
     * comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )*
     * term           → factor ( ( "-" | "+" ) factor )*
     * factor         → unary ( ( "/" | "*" ) unary )*
     * unary          → ( "!" | "-" ) unary | primary
     * primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER
     */

}
