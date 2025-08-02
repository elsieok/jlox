package jlox;

import java.util.List;

class jloxFunction implements jloxCallable{
    // Challenge 10.2 syntax changed
    private final FunctionDeclaration declaration;
    private final Environment closure;
    private final boolean isInitialiser;
    private final boolean isGetter;

    jloxFunction(FunctionDeclaration declaration, Environment closure, boolean isInitialiser, boolean isGetter) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitialiser = isInitialiser;
        if (isInitialiser) {
            this.isGetter = false;
        } else {
            this.isGetter = isGetter;
        }
        
    }

    jloxFunction bind(jloxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new jloxFunction(declaration, environment, isInitialiser, isGetter);
    }

    boolean getIsGetter() {
        return isGetter;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);

        List<Token> params = declaration.getParams();
        if (!isGetter) { // challenge 12.2
            for (int i = 0; i < params.size(); i++) {
                environment.define(params.get(i).lexeme, arguments.get(i));
            }
        }

        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        } catch (Return returnValue) {
            if (isInitialiser) return closure.getAt(0, "this");


            return returnValue.value;
        }

        if (isInitialiser) return closure.getAt(0, "this");
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
