package jlox;

import java.util.List;
import java.util.Random;

public class MathsLibrary {
    // mathematical constants
    public static final double PI = Math.PI;
    public static final double E = Math.E;

    private static final Random random = new Random();

    public static class sqrt implements jloxCallable{
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);
            if (!(arg instanceof Double)) {
                throw new RuntimeError(null, "Argument must be a number.");
            }

            double value = (Double)arg;
            if (value < 0) {
                throw new RuntimeError(null, "Cannot take square root of negative number.");
            }

            return Math.sqrt(value);
        }

        @Override
        public String toString() {
            return "<native fn sqrt>";
        }
    }

    public static class pow implements jloxCallable{
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object base = arguments.get(0);
            Object exp = arguments.get(1);

            if (!(base instanceof Double)) {
                throw new RuntimeError(null, "Base must be a number.");
            }

            if(!(exp instanceof Double)) {
                throw new RuntimeError(null, "Exponent must be a number.");
            }

            return Math.pow((Double)base, (Double)exp);
        }

        @Override
        public String toString() {
            return "<native fn pow>";
        }
    }

    public static class abs implements jloxCallable{
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof Double)) {
                throw new RuntimeError(null, "Argument must be a number.");
            }

            return Math.abs((Double)arg);
        }

        @Override
        public String toString() {
            return "<native fn abs>";
        }
    }

    public static class floor implements jloxCallable{
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof Double)) {
                throw new RuntimeError(null, "Argument must be a number.");
            }

            return Math.floor((Double)arg);
        }

        @Override
        public String toString() {
            return "<native fn floor>";
        }
    }

    public static class ceil implements jloxCallable{
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof Double)) {
                throw new RuntimeError(null, "Argument must be a number.");
            }

            return Math.ceil((Double)arg);
        }

        @Override
        public String toString() {
            return "<native fn ceil>";
        }
    }

    public static class random implements jloxCallable{
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            return random.nextDouble();
        }

        @Override
        public String toString() {
            return "<native fn random>";
        }
    }

    public static class randomInt implements jloxCallable{
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object min = arguments.get(0);
            Object max = arguments.get(1);

            if (!(min instanceof Double)) {
                throw new RuntimeError(null, "Minimum must be an integer.");
            }

            if(!(max instanceof Double)) {
                throw new RuntimeError(null, "Maximum must be an integer.");
            }

            int minInt = ((Double)min).intValue();
            int maxInt = ((Double)max).intValue();

            if (minInt > maxInt) {
                throw new RuntimeError(null, "Min cannot be greater than max.");
            }

            return (double)(random.nextInt(maxInt - minInt + 1) + minInt);
        }

        @Override
        public String toString() {
            return "<native fn randomInt>";
        }
    }

    public static class sin implements jloxCallable{
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof Double)) {
                throw new RuntimeError(null, "Argument must be a number.");
            }

            return Math.sin((Double)arg);
        }

        @Override
        public String toString() {
            return "<native fn sin>";
        }
    }

    public static class cos implements jloxCallable{
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof Double)) {
                throw new RuntimeError(null, "Argument must be a number.");
            }

            return Math.cos((Double)arg);
        }

        @Override
        public String toString() {
            return "<native fn cos>";
        }

    }

    public static class tan implements jloxCallable{
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);

            if (!(arg instanceof Double)) {
                throw new RuntimeError(null, "Argument must be a number.");
            }

            return Math.tan((Double)arg);
        }

        @Override
        public String toString() {
            return "<native fn tan>";
        }
    }
    
}
