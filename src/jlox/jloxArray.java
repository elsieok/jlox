package jlox;

import java.util.Arrays;
import java.util.List;

class jloxArray extends jloxInstance{
    private final Object[] elements;

    jloxArray(int size) {
        super(null);
        elements = new Object[size];
    }

    jloxArray(Object[] list) {
        super(null);
        elements = list;
    }

    Object[] getElements() {
        return elements;
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
            case "sort" -> {
                return new jloxCallable() {
                    @Override
                    public int arity() {
                        return 0;
                    }
                    
                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        boolean hasDouble = false;
                        boolean hasString = false;
            
                        for (Object element : elements) {
                            if (element == null) continue;

                            if (element instanceof Double) {
                                hasDouble = true;
                            } else if (element instanceof String) {
                                hasString = true;
                            } else {
                                throw new RuntimeError(name, "Can only sort arrays of all numbers or all strings.");
                            }

                            if (hasString && hasDouble) {
                                throw new RuntimeError(name, "Can only sort arrays of all numbers or all strings.");
                            }
                        }

                        final boolean isDouble = hasDouble;  
                        Object[] sorted = elements.clone();
                        Arrays.sort(sorted, (a, b) -> {
                            if (a == null && b == null) return 0;
                            if (a == null) return 1;
                            if (b == null) return -1;
                            if (isDouble) {
                                return Double.compare((Double)a, (Double)b);
                            } else { // hasString
                                return ((String)a).compareTo((String)b);
                            }
                        });
                        
                        return new jloxArray(sorted);
                    }
                };
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
            builder.append(CoreLibrary.stringify(elements[i]));
        }
        builder.append("]");
        return builder.toString();
    }

}
