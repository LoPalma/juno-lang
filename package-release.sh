#!/bin/bash
if [ $# -ne 1 ]; then
  echo "Usage: ./package-release.sh <version>"
  echo "Example: ./package-release.sh 0.1.0"
  exit 1
fi
VERSION=$1
echo "Packaging Juno v$VERSION for release..."
# Clean up old releases
rm -rf release/
# Create release directory structure
mkdir -p release/juno/lib
# Copy necessary files
echo "Copying files..."
cp juno release/juno/
cp junoc release/juno/
cp target/juno-1.0.0-SNAPSHOT.jar release/juno/lib/
# Copy any other lib dependencies if they exist
if [ -d "lib" ] && [ "$(ls -A lib)" ]; then
  cp -r lib/* release/juno/lib/
fi
# Create tarball
cd release
tar -czf juno-linux.tar.gz juno/
cd ..
echo ""
echo "Release package created: release/juno-linux.tar.gz"
echo ""
echo "Contents of package:"
tar -tzf release/juno-linux.tar.gz
echo ""
echo "Next steps:"
echo "1. Go to https://github.com/LoPalma/juno-lang/releases/new"
echo "2. Create a new tag: v$VERSION"
echo "3. Set release title: Juno v$VERSION"
echo "4. Upload: release/juno-linux.tar.gz"
