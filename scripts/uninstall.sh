#!/bin/bash
set -e

# Allow custom install location (should match what was used in install.sh)
INSTALL_PREFIX="${1:-/usr/local}"
INSTALL_BIN="${INSTALL_PREFIX}/bin"
INSTALL_LIB="${INSTALL_PREFIX}/lib/jlox"

echo "Uninstalling jlox from ${INSTALL_PREFIX}..."

# Check if jlox is actually installed
if [ ! -f "${INSTALL_BIN}/jlox" ] && [ ! -d "${INSTALL_LIB}" ]; then
    echo "jlox doesn't appear to be installed in ${INSTALL_PREFIX}"
    echo "If you installed to a different location, specify it:"
    echo "  ./scripts/uninstall.sh /path/to/install/prefix"
    exit 1
fi

# Function to safely remove file/directory
safe_remove() {
    local path="$1"
    local description="$2"
    
    if [ -e "$path" ]; then
        if sudo rm -rf "$path" 2>/dev/null; then
            echo "✓ Removed $description"
        else
            echo "✗ Failed to remove $description ($path)"
            echo "  You may need to remove it manually"
        fi
    else
        echo "- $description not found (already removed?)"
    fi
}

# Remove the launcher script
safe_remove "${INSTALL_BIN}/jlox" "launcher script"

# Remove the lib directory
safe_remove "${INSTALL_LIB}" "library directory"

# Check if we can remove empty parent directories
# Only remove if they're empty (won't remove if other software uses them)
if [ -d "${INSTALL_PREFIX}/lib" ]; then
    if sudo rmdir "${INSTALL_PREFIX}/lib" 2>/dev/null; then
        echo "✓ Removed empty lib directory"
    fi
fi

echo ""
echo "jlox has been uninstalled!"
echo ""

# Verify uninstallation
if command -v jlox &> /dev/null; then
    echo "WARNING: 'jlox' command is still available in your PATH."
    echo "This might be because:"
    echo "  - You have multiple installations"
    echo "  - The binary is cached in your shell"
    echo "  - It's installed in a different location"
    echo ""
    echo "Try running: hash -r"
    echo "Or restart your terminal session."
else
    echo "✓ Verified: jlox command is no longer available"
fi