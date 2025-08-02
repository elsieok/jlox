package jlox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringLibrary {

    public static class len implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);
            if (!(arg instanceof String)) {
                jlox.error(null, "Argument must be a string.");
            }

            return (double)((String)arg).length();
        }

        public String toString() {
            return "<native fn len>";
        }

    }

    public static class substr implements jloxCallable {
        @Override
        public int arity() {
            return 3;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object str = arguments.get(0);
            Object start = arguments.get(1);
            Object end = arguments.get(2);

            if (!(str instanceof String)) {
                throw new RuntimeError(null, "First argument must be a string.");
            }

            if (!(start instanceof Double)) {
                throw new RuntimeError(null, "Start must be a number.");
            }

            if (!(end instanceof Double)) {
                throw new RuntimeError(null, "End must be a number.");
            }

            String s = (String)str;
            int startIdx = ((Double)start).intValue();
            int endIdx = ((Double)end).intValue();

            if (startIdx < 0 || endIdx > s.length() || startIdx > endIdx) {
                throw new RuntimeError(null, "Invalid substring indices.");
            }

            return s.substring(startIdx, endIdx);
        }

        @Override
        public String toString() {
            return "<native fn substr>";
        }

    }

    public static class toUpper implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof String)) {
                throw new RuntimeError(null, "Argument must be a string.");
            }

            return ((String)arg).toUpperCase();
        }

        @Override
        public String toString() {
            return "<native fn toUpper>";
        }
    }

    public static class toLower implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof String)) {
                throw new RuntimeError(null, "Argument must be a string.");
            }

            return ((String)arg).toLowerCase();
        }

        @Override
        public String toString() {
            return "<native fn toLower>";
        }
    }

    public static class split implements jloxCallable {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object str = arguments.get(0);
            Object regex = arguments.get(1);

            if (!(str instanceof String) || !(regex instanceof String)) {
                throw new RuntimeError(null, "Both arguments must be strings.");
            }

            String[] parts = ((String)str).split((String)regex);
            List<Object> result = new ArrayList<>();
            result.addAll(Arrays.asList(parts));

            return result;
        }

        @Override
        public String toString() {
            return "<native fn split>";
        }
    }

    public static class stringify implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object nonString = arguments.get(0);
            if (nonString == null) {
                return "nil";
            }
            if (nonString instanceof Double) {
                String text = nonString.toString();
                if (text.endsWith(".0")) {
                    text = text.substring(0, text.length() - 2);
                }
                return text;
            }
            return nonString.toString();
        }

        @Override
        public String toString() {
            return "<native fn toString>";
        }

    }
    
}
