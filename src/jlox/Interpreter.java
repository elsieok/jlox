package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    private static class BreakException extends RuntimeException {
    // This exception is used to break out of loops in the interpreter.
    // It does not carry any additional information.
    }

    private static class ContinueException extends RuntimeException {
    // This exception is used to continue to the next iteration of loops in the interpreter.
    // It does not carry any additional information.
    }

    Interpreter() {
        globals.define("class", new jloxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            jlox.runtimeError(error);
        }
    }

    // Challenge 8.1: Interpret an expression
    String interpretExpression(Expr expr) {
        try {
            String value = stringify(evaluate(expr));
            return value;
        } catch (RuntimeError error) {
            jlox.runtimeError(error);
            return null;
        }
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance =locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        
        return value;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case STAR -> {
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            }
            case SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                if ((double) right == 0) {
                    throw new RuntimeError(expr.operator, "Division by zero is undefined.");
                }
                return (double)left / (double)right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            }
            case MINUS -> {
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            }
            case PERCENT -> {
                if ((double) right == 0) {
                    throw new RuntimeError(expr.operator, "Modulo by zero is undefined.");
                }
                return (double)left % (double)right;
            }
            case GREATER -> {
                if (left instanceof String && right instanceof String) {
                    return ((String)left).compareTo((String)right) > 0;
                }

                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            }
            case GREATER_EQUAL -> {
                if (left instanceof String && right instanceof String) {
                    return ((String)left).compareTo((String)right) >= 0;
                }

                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            }
            case LESS -> {
                if (left instanceof String && right instanceof String) {
                    return ((String)left).compareTo((String)right) < 0;
                }

                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            }
            case LESS_EQUAL -> {
                if (left instanceof String && right instanceof String) {
                    return ((String)left).compareTo((String)right) <= 0;
                }

                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            }
            case BANG_EQUAL -> {
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return isEqual(left, right);
            }
            case COMMA -> {
                return right;
            }
        }

        // Unreachable code
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof jloxCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        jloxCallable function = (jloxCallable)callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    // Challenge 10.2
    @Override
    public Object visitFunctionExpr(Expr.Function expr) {
        FunctionDeclarationAdapter someFunctionExpr = new FunctionDeclarationAdapter(expr);
        jloxFunction function = new jloxFunction(someFunctionExpr, environment);
        return function;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
        if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        Object condition = evaluate(expr.condition);

        if (isTruthy(condition)) {
            return evaluate(expr.thenExpr);
        } else {
            return evaluate(expr.elseExpr);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG -> {
                return !isTruthy(right);
            }
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            }
        }

        //Unreachable
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }
    
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override // Challenge 9.3
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new BreakException();
    }

    @Override // Challenge 9.3
    public Void visitContinueStmt(Stmt.Continue stmt) {
        throw new ContinueException();
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitForDesugaredStmt(Stmt.ForDesugared stmt) {
        try {
            while (isTruthy(evaluate(stmt.condition))) {
                try {
                    execute(stmt.body);
                    execute(stmt.increment);
                } catch (ContinueException e) {
                    execute(stmt.increment);
                }

            }
            
        } catch (BreakException e) {
        }

        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        // Challenge 10.2, syntax changed from original
        FunctionDeclarationAdapter someFunctionExpr = new FunctionDeclarationAdapter(stmt);
        jloxFunction function = new jloxFunction(someFunctionExpr, environment);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override 
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }
    
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }
    
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value;
        if (stmt.initialiser != null) {
            value = evaluate(stmt.initialiser);
        } else {
            value = Environment.uninitialisedValue();
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }
    
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        try {
            while (isTruthy(evaluate(stmt.condition))) {
                try {
                    execute(stmt.body);
                } catch (ContinueException ex) {
                // continue
                }
            } 
        } catch (BreakException ex) {
            // break out of loop
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }
    
    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }
    
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

}

// Challenge 7.1: Done, extended to support lexographical comparison
// Challenge 7.2: Didn't like it, felt inconsistent, would rather an error was raised, and require both operands to be of type string to concatenate
// Challenge 7.3: Done, added error, division by zero is undefined