# Java compiler
JAVAC = javac

# Source and output directories
SRC_DIR = src
BIN_DIR = bin

# Find all Java files under src/ and subdirectories recursively
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# Default target
all: build run

build:
	$(JAVAC) -d bin $(SOURCES)


run:
	java -cp $(BIN_DIR) jlox.jlox

# Clean compiled files
clean:
	rm -rf $(BIN_DIR)/*
