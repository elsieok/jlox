#!/bin/bash

set -e

# allow custom install location
INSTALL_PREFIX="${1:-/usr/local}"
INSTALL_BIN="${INSTALL_PREFIX}/bin"
INSTALL_LIB="${INSTALL_PREFIX}/lib"

echo "Installing jlox to ${INSTALL_PREFIX}..."

# check if distribution exists
if [ ! -f "dist/lib/jlox.jar" ]; then
    echo "Distribution not found. Run './scripts/package.sh' first."
    exit 1
fi

# create directories (need sudo for system directories)
sudo mkdir -p "${INSTALL_LIB}"
sudo mkdir -p "${INSTALL_BIN}"

# install JAR
sudo cp dist/lib/jlox.jar "${INSTALL_LIB}/"

# install launcher
sudo cp dist/bin/jlox "${INSTALL_BIN}/"

echo "jlox installed successfully!"
echo ""
echo "Usage:"
echo "  jlox              # Start REPL"
echo "  jlox script.jlox   # Run a script"
echo "  jlox --help       # Show help (if you implement it)"