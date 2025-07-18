package jlox;

// Challenge 9.3

class BreakException extends RuntimeException {
    // This exception is used to break out of loops in the interpreter.
    // It does not carry any additional information.
}

class ContinueException extends RuntimeException {
    // This exception is used to continue to the next iteration of loops in the interpreter.
    // It does not carry any additional information.
}