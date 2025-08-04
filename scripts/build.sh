#!/bin/bash

# exit immediately if any command fails
set -e

echo "Building jlox..."

# clean previous builds
rm -rf build/classes/*
rm -rf build/jar/*

# compile Java sources
echo "Compiling Java sources"
javac -d build/classes src/jlox/*.java

# create JAR
echo "Creating JAR..."
jar cfm build/jar/jlox.jar MANIFEST.MF -C build/classes .

echo "Build complete!"