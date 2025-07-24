package jlox;

import java.util.List;

class jloxFunction implements jloxCallable{
    // Challenge 10.2 syntax changed
    private final FunctionDeclaration declaration;
    private final Environment closure;

    jloxFunction(FunctionDeclaration declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);

        List<Token> params = declaration.getParams();
        for (int i = 0; i < params.size(); i++) {
            environment.define(params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }

        return null;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public String toString() {
        Token name = declaration.getName();
        return "<fn " + (name == null ? "anonymous" : name.lexeme) + ">";
    }

}
