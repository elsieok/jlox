# Java compiler
JAVAC = javac

# Source and output directories
SRC_DIR = src
BIN_DIR = bin

# Find all Java files under src/ and subdirectories recursively
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# Default target
all: gen run

gen:
	java -cp $(BIN_DIR) tool.GenerateAST /Users/elsieok/Documents/Projects/craftingInterpreters/src/jlox

fullgen:
	$(JAVAC) -d bin src/tool/GenerateAST.java
	java -cp $(BIN_DIR) tool.GenerateAST /Users/elsieok/Documents/Projects/craftingInterpreters/src/jlox

runp:
	java -cp $(BIN_DIR) jlox.jlox

runf:
	java -cp $(BIN_DIR) jlox.jlox testLoxFile.jlox

runfl:
	java -cp $(BIN_DIR) jlox.jlox testLibFile.jlox

runfp:
	java -cp $(BIN_DIR) jlox.jlox testPrintFunFile.jlox

# Clean compiled files
clean:
	rm -rf $(BIN_DIR)/*
	$(JAVAC) -d bin $(SOURCES)
