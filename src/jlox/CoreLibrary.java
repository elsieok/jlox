package jlox;

import java.util.List;

class CoreLibrary {
    static class clock implements jloxCallable {
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
            return "<native fn clock>";
        }
    }

    static class print implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            if (arguments.get(0) == null) {
                System.out.println("nil");
            } else if (arguments.get(0) instanceof String printed) {
                System.out.println(printed);
            } else {
                String printed = stringify(arguments.get(0));
                System.out.println(printed);
            }
            return null;
        }

        @Override
        public String toString() {
            return "<native fn print>";
        }

    }

    static class stringify implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object something = arguments.get(0);
            return stringify(something);
        }

        @Override
        public String toString() {
            return "<native fn stringify>";
        }

    }
  
    public static String stringify(Object object) {
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
