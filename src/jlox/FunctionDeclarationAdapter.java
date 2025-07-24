package jlox;

// Challenge 10.2

import java.util.List;

class FunctionDeclarationAdapter implements FunctionDeclaration{
    private final Stmt.Function stmtFunction;
    private final Expr.Function exprFunction;

    public FunctionDeclarationAdapter(Stmt.Function stmtFunction){
        this.stmtFunction = stmtFunction;
        this.exprFunction = null;
    }

    public FunctionDeclarationAdapter(Expr.Function exprFunction){
        this.stmtFunction = null;
        this.exprFunction = exprFunction;
    }

    @Override
    public List<Token> getParams() {
        return stmtFunction != null ? stmtFunction.params : exprFunction.params;
    }

    @Override
    public List<Stmt> getBody() {
        return stmtFunction != null ? stmtFunction.body : exprFunction.body;
    }

    @Override
    public Token getName() {
        return stmtFunction != null ? stmtFunction.name : null;
    }
}
