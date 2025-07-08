package jlox;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

<<<<<<< HEAD
=======
    @Override
>>>>>>> 2950143 (parsing expressions done + challenges finished)
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
