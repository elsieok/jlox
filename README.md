# JLox - A Tree-Walking Interpreter

A Java implementation of the Lox programming language from [Crafting Interpreters](https://craftinginterpreters.com/).

## Prerequisites

- Java 8 or higher
- Unix-like system (Linux, macOS) or Windows with bash support

## Quick Start

### Option 1: Build and Run Locally
```bash
# Clone the repository
git clone https://github.com/yourusername/jlox.git
cd jlox

# Build the project
./scripts/build.sh

# Run directly
java -jar build/jar/jlox.jar

# Or create a distribution
./scripts/package.sh
./dist/bin/jlox
```

### Option 2: Install System-wide
```bash
# Build and package
./scripts/build.sh
./scripts/package.sh

# Install to /usr/local (requires sudo)
sudo ./scripts/install.sh

# Or install to custom location
./scripts/install.sh ~/local

# Now you can run jlox from anywhere
jlox
```

## Usage

### Interactive REPL
```bash
jlox
```

### Run a script
```bash
jlox script.lox
```

### Try the examples
```bash
# After building and packaging
jlox dist/examples/hello.lox
jlox dist/examples/fibonacci.lox
jlox dist/examples/classes.lox

# Or copy examples to current directory
cp dist/examples/*.lox .
jlox hello.lox
```

### Custom JVM options
```bash
# Set memory limit
export JLOX_OPTS="-Xmx1g"
jlox large_script.lox
```

## Build Scripts

- **`build.sh`**: Compiles Java sources and creates JAR
- **`package.sh`**: Creates a complete distribution with launchers
- **`install.sh`**: Installs jlox system-wide
- **`uninstall.sh`**: Removes jlox from system

### Uninstalling

```bash
# Uninstall from default location (/usr/local)
sudo ./scripts/uninstall.sh

# Uninstall from custom location
./scripts/uninstall.sh ~/local
```

## Windows Users

The package script creates both Unix (`jlox`) and Windows (`jlox.bat`) launchers. Windows users should use:

```cmd
dist\bin\jlox.bat
```

Or after installation:
```cmd
jlox.bat
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test with `./scripts/build.sh`
5. Submit a pull request

## License

MIT License

## Acknowledgments


Based on the excellent book [Crafting Interpreters](https://craftinginterpreters.com/) by Robert Nystrom.
