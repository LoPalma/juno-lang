#!/bin/bash

# Clean Maven runner for C-like JVM Language Compiler
# Usage: ./mvn-run.sh <source-file> [--debug-ast]

# Check if source file is provided
if [ $# -lt 1 ]; then
    echo "Usage: ./mvn-run.sh <source-file> [--debug-ast]"
    exit 1
fi

# Run with Maven but suppress most noise
MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn" \
mvn exec:java \
    -Dexec.args="$*" \
    -q \
    2>/dev/null