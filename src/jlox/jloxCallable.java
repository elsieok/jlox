package jlox;

import java.util.List;

interface jloxCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
