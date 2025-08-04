#!/bin/bash

set -e

echo "Packaging jlox distribution..."

# build scripts first
./scripts/build.sh

# copy JAR to districution lib dir
cp build/jar/jlox.jar dist/lib/

# create Unix launcher script
cat > dist/bin/jlox << 'EOF'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JLOX_HOME="$(dirname "$SCRIPT_DIR")"
JLOX_OPTS="${JLOX_OPTS:-"-Xmx512m"}"

if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH" >&2
    exit 1
fi

exec java ${JLOX_OPTS} -jar "${JLOX_HOME}/lib/jlox.jar" "$@"
EOF

# create Windows launcher script
cat > dist/bin/jlox.bat << 'EOF'
@echo off
set SCRIPT_DIR=%~dp0
set JLOX_HOME=%SCRIPT_DIR%..
if not defined JLOX_OPTS set JLOX_OPTS=-Xmx512m

java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH >&2
    exit /b 1
)

java %JLOX_OPTS% -jar "%JLOX_HOME%\lib\jlox.jar" %*
EOF

# make Unix script executable
chmod +x dist/bin/jlox

# copy example files (if they exist)
cp examples/*.lox dist/examples/ 2>/dev/null || echo "No example files found"

echo "Distribution created in dist/"
echo "To test: ./dist/bin/jlox"
echo "To install system-wide: sudo ./scripts/install.sh"