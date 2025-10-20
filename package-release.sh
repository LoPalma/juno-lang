#!/usr/bin/env bash

set -e  # Exit on error

VERSION="0.3.3-alpha"
RELEASE_DIR="release/juno-$VERSION"
ARCHIVE_NAME="juno-$VERSION-linux.tar.gz"

echo "=== Juno Release Packager ==="
echo "Version: $VERSION"
echo ""

# Clean previous release
if [ -d "$RELEASE_DIR" ]; then
    echo "Cleaning previous release directory..."
    rm -rf "$RELEASE_DIR"
fi

# Build with Maven
echo "Building project with Maven..."
mvn clean package -DskipTests

# Verify the JAR was created
if [ ! -f "target/juno-$VERSION.jar" ]; then
    echo "Error: JAR file not found: target/juno-$VERSION.jar"
    echo "Available JARs:"
    ls -lh target/*.jar
    exit 1
fi

# Create release directory structure
echo "Creating release directory structure..."
mkdir -p "$RELEASE_DIR"
mkdir -p "$RELEASE_DIR/lib"
mkdir -p "$RELEASE_DIR/examples"

# Copy the main JAR (contains compiler + stdlib)
echo "Copying compiler JAR..."
cp "target/juno-$VERSION.jar" "$RELEASE_DIR/lib/"

# ASM is now bundled in the JAR by maven-shade-plugin, no need to copy separately!

# Create junoc wrapper
echo "Creating junoc wrapper..."
cat > "$RELEASE_DIR/junoc" << 'EOF'
#!/usr/bin/env bash

# Determine installation paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Find the Juno JAR
JUNO_JAR="$SCRIPT_DIR/lib/juno-*.jar"
JUNO_JAR=$(ls $JUNO_JAR 2>/dev/null | head -1)

if [ -z "$JUNO_JAR" ]; then
    echo "Error: Juno compiler JAR not found in $SCRIPT_DIR/lib/"
    exit 1
fi

# Build classpath (just the fat JAR - ASM is bundled inside!)
CLASSPATH="$JUNO_JAR"

# Run the compiler
java -cp "$CLASSPATH" com.juno.Main "$@"
EOF

chmod +x "$RELEASE_DIR/junoc"

# Create jpm wrapper
echo "Creating jpm wrapper..."
cp jpm "$RELEASE_DIR/jpm"
chmod +x "$RELEASE_DIR/jpm"

# Copy example files
echo "Copying examples..."
cp examples/hello.juno "$RELEASE_DIR/examples/"
cp examples/fibonacci.juno "$RELEASE_DIR/examples/"

# Create README
echo "Creating README..."
cat > "$RELEASE_DIR/README.md" << 'EOF'
# Juno Programming Language

Version: $VERSION

## Installation

1. Extract this archive:
   ```bash
   tar -xzf juno-$VERSION-linux.tar.gz
   ```

2. Add to PATH:
   ```bash
   export PATH="$PWD/juno-$VERSION:$PATH"
   ```

   Or install system-wide:
   ```bash
   sudo cp -r juno-$VERSION /opt/juno
   sudo ln -s /opt/juno/junoc /usr/local/bin/junoc
   sudo ln -s /opt/juno/jpm /usr/local/bin/jpm
   ```

## Quick Start

Compile a Juno program:
```bash
junoc hello.juno
```

Run a Juno program:
```bash
jpm exec Hello
```

Compile and run:
```bash
jpm run hello.juno
```

Initialize a new project:
```bash
jpm init my-project
cd my-project
jpm run src/main.juno
```

## Examples

See the `examples/` directory for sample programs.

## Documentation

Visit: https://juno-lang.org

## License

See LICENSE file.
EOF

# Copy license
if [ -f LICENSE ]; then
    cp LICENSE "$RELEASE_DIR/"
fi

# Create archive
echo "Creating tarball..."
cd release
tar -czf "$ARCHIVE_NAME" "juno-$VERSION"
cd ..

echo ""
echo "--- Release Package Created ---"
echo "Location: release/$ARCHIVE_NAME"
echo "Size: $(du -h release/$ARCHIVE_NAME | cut -f1)"
echo ""
echo "Contents:"
tar -tzf "release/$ARCHIVE_NAME" | head -20
echo "..."
echo ""
echo "To test the release:"
echo "  cd /tmp"
echo "  tar -xzf $(pwd)/release/$ARCHIVE_NAME"
echo "  cd juno-$VERSION"
echo "  ./jpm run examples/hello.juno"
echo ""
echo "Done!"
