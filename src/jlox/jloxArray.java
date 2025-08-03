package jlox;

import java.util.List;

class jloxArray extends jloxInstance{
    private final Object[] elements;

    jloxArray(int size) {
        super(null);
        elements = new Object[size];
    }

    @Override
    Object get(Token name) {
        switch (name.lexeme) {
            case "get" -> {
                return new jloxCallable() {
                    @Override
                    public int arity() {
                        return 1;
                    }
                    
                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        int index = (int)(double)arguments.get(0);
                        return elements[index];
                    }
                    
                };
            }
            case "set" -> {
                return new jloxCallable() {
                    @Override
                    public int arity() {
                        return 2;
                    }
                    
                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        int index = (int)(double)arguments.get(0);
                        Object value = arguments.get(1);
                        return elements[index] = value;
                    }
                };
            }
            case "length" -> {
                return (double)elements.length;
            }
            default -> {
            }
        }

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    @Override
    void set(Token name, Object value) {
        throw new RuntimeError(name, "Can't add properties to arrays.");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < elements.length; i++) {
            if (i != 0) builder.append(", ");
            builder.append(elements[i] == null ? "nil" : elements[i]);
        }
        builder.append("]");
        return builder.toString();
    }

}
