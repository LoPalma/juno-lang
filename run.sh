#!/bin/bash

# Clean runner script for C-like JVM Language Compiler
# Usage: ./run.sh <source-file> [--debug-ast]

# Check if source file is provided
if [ $# -lt 1 ]; then
    echo "Usage: ./run.sh <source-file> [--debug-ast]"
    exit 1
fi

# Check if JAR exists, build if needed
JAR_FILE="target/clike-jvm-lang-1.0.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "Building project..."
    mvn package -q >/dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo "Build failed!"
        exit 1
    fi
fi

# Run the compiler with clean output
java -jar "$JAR_FILE" "$@"