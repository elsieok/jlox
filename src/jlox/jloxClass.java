package jlox;

import java.util.List;
import java.util.Map;

class jloxClass extends jloxInstance implements jloxCallable {
    final String name;
    private final Map<String, jloxFunction> methods;
    private final jloxClass metaclass;

    jloxClass(String name, Map<String, jloxFunction> methods, jloxClass metaclass) {
        super(metaclass);
        this.name = name;
        this.methods = methods;
        this.metaclass = metaclass;
    }

    jloxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    @Override
    Object get(Token name) {
        if (metaclass.methods.containsKey(name.lexeme)) {
            return metaclass.methods.get(name.lexeme);
        }
        return super.get(name);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        jloxInstance instance = new jloxInstance(this);

        jloxFunction initialiser = findMethod("init");
        if (initialiser != null) {
            initialiser.bind(instance).call(interpreter, arguments);
        }
        
        return instance;
    }

    @Override
    public int arity() {
        jloxFunction initialiser = findMethod("init");
        if (initialiser == null) return 0;
        return initialiser.arity();
    }

    @Override
    public String toString() {
        return name;
    }
    
}
