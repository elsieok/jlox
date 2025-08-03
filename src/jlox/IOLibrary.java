package jlox;

import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class IOLibrary {
    private static final Scanner scanner = new Scanner(System.in);

    static class input implements jloxCallable {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            try {
                return scanner.nextLine();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "<native fn input>";
        }

    }

    static class inputPrompt implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object prompt = arguments.get(0);
            System.out.println(stringify(prompt));
            try {
                return scanner.nextLine();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "<native fn prompt>";
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
    
    static class readFile implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object filename = arguments.get(0);
            if (!(filename instanceof String)) {
                throw new RuntimeError(null, "Filename must be a string.");
            }

            try {
                byte[] bytes = Files.readAllBytes(Paths.get((String)filename));
                return new String(bytes);
            } catch (IOException e) {
                throw new RuntimeError(null, "Could not read file: " + e.getMessage());
            }
        }

        @Override
        public String toString() {
            return "<native fn readFile>";
        }

    }
    
    static class writeFile implements jloxCallable {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object filename = arguments.get(0);
            Object content = arguments.get(1);
            
            if (!(filename instanceof String)) {
                throw new RuntimeError(null, "Filename must be a string.");
            }

            try {
                Files.write(Paths.get((String)filename), stringify(content).getBytes());
                return null;
            } catch (IOException e) {
                throw new RuntimeError(null, "Could not write file: " + e.getMessage());
            }
        }

        @Override
        public String toString() {
            return "<native fn writeFile>";
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
    
    static class appendFile implements jloxCallable {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object filename = arguments.get(0);
            Object content = arguments.get(1);

            if (!(filename instanceof String)) {
                throw new RuntimeError(null, "Filename must be a string.");
            }
            
            try {
                Files.write(Paths.get((String)filename), 
                           stringify(content).getBytes(),
                           java.nio.file.StandardOpenOption.CREATE,
                           java.nio.file.StandardOpenOption.APPEND);
                return null;
            } catch (IOException e) {
                throw new RuntimeError(null, "Could not append to file: " + e.getMessage());
            }
        }

        @Override
        public String toString() {
            return "<native fn appendFile>";
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
    
    static class fileExists implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object filename = arguments.get(0);

            if (!(filename instanceof String)) {
                throw new RuntimeError(null, "Filename must be a string.");
            }

            return Files.exists(Paths.get((String)filename));
        }

        @Override
        public String toString() {
            return "<native fn fileExists>";
        }

    }
    
    static class deleteFile implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object filename = arguments.get(0);

            if (!(filename instanceof String)) {
                throw new RuntimeError(null, "Filename must be a string.");
            }

            try {
                return Files.deleteIfExists(Paths.get((String)filename));
            } catch (IOException e) {
                throw new RuntimeError(null, "Could not delete file: " + e.getMessage());
            }
        }

        @Override
        public String toString() {
            return "<native fn deleteFile>";
        }

    }
    
    static class printNoNewline implements jloxCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            System.out.print(stringify(arguments.get(0)));
            return null;
        }

        @Override
        public String toString() {
            return "<native fn printn>";
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
    
}
