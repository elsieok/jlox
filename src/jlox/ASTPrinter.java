package jlox;

class ASTPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
<<<<<<< HEAD
=======
    public String visitTernaryExpr(Expr.Ternary expr) {
        return parenthesiseT(expr.op1.lexeme, expr.op2.lexeme, expr.left, expr.mid, expr.right);
    }

    @Override
>>>>>>> 2950143 (parsing expressions done + challenges finished)
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesise(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesise("group", expr.expression);
    }
        
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }
    
    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesise(expr.operator.lexeme, expr.right);
    }

<<<<<<< HEAD
=======
    private String parenthesiseT(String name1, String name2, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name1).append(name2);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

>>>>>>> 2950143 (parsing expressions done + challenges finished)
    private String parenthesise(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    // public static void main(String[] args) {
    //     Expr expression = new Expr.Binary(
    //         new Expr.Unary(
    //             new Token(TokenType.MINUS, "-", null, 1),
    //             new Expr.Literal(123)
    //             ),
    //         new Token(TokenType.STAR, "*", null, 1),
    //         new Expr.Grouping(
    //             new Expr.Literal(45.67)
    //             ));

    //     System.out.println(new ASTPrinter().print(expression));
    // }
}
