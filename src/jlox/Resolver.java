package jlox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import jlox.Stmt.ForDesugared;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, VariableInfo>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    // challenge 11.3
    class VariableInfo {
        Token token;
        boolean defined;
        boolean used;

        VariableInfo (Token token, boolean defined, boolean used){
            this.token = token;
            this.defined = defined;
            this.used = used;
        }
    }

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
        resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        
        for (Token param : function.params) {
            declare(param);
            define(param);
        }

        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void resolveFunction(Expr.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        
        for (Token param : function.params) {
            declare(param);
            define(param);
        }

        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitForDesugaredStmt(ForDesugared stmt) {
        resolve(stmt.condition);
        resolve(stmt.increment);
        resolve(stmt.body);
        return null;
    }
    
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            jlox.error(stmt.keyword, "Can't return from top-level code.");
        }
        if (stmt.value != null) {
        resolve(stmt.value);
        }

        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);

        if (stmt.initialiser != null) {
            resolve(stmt.initialiser);
        }

        define(stmt.name);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
        resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitFunctionExpr(Expr.Function expr ) {
        resolveFunction(expr, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitTernaryExpr(Expr.Ternary expr) {
        resolve(expr.condition);
        resolve(expr.thenExpr);
        resolve(expr.elseExpr);

        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty()) {
            VariableInfo info = scopes.peek().get(expr.name.lexeme);
            if (info != null && Objects.equals(info.defined, Boolean.FALSE)) {
                jlox.error(expr.name, "Can't read local variable in its own initializer.");
            }
        }

        resolveLocal(expr, expr.name);
        use(expr.name);

        return null;
    }
    
    @SuppressWarnings("Convert2Diamond")
    private void beginScope() {
        scopes.push(new HashMap<String, VariableInfo>());
    }

    private void endScope() {
        Map<String, VariableInfo> scope = scopes.pop();
        for (Map.Entry<String, VariableInfo> entry : scope.entrySet()) {
            VariableInfo info = entry.getValue();

            if (!info.used) {
                jlox.error(info.token, "Variable declared but never used.");
            }
        }
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, VariableInfo> scope = scopes.peek();

        if (scope.containsKey(name.lexeme)) {
            jlox.error(name, "Already a variable with this name in this scope.");
        }

        scope.put(name.lexeme,  new VariableInfo(name, false, false));
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, VariableInfo> scope = scopes.peek();
        scope.get(name.lexeme).defined = true;
    }

    // challenge 11.3
    private void use(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, VariableInfo> scope = scopes.peek();
        VariableInfo info = scope.get(name.lexeme);
        if (info != null) {
            info.used = true;
        }
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

}

// to complete challenge 11.4, you'd probably have to add another field to the AST node but that seems pretty complicated considering you'd ave to keep track of functions and var/assign exprs as well. maybe come back to this later...
