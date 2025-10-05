#!/bin/bash

INSTALL_LOCAL=true
REPO="LoPalma/juno-lang"

echo "Juno installer"
echo ""

# Ask about local installation
while true; do
  echo "Would you like to install Juno locally? [Y/n]"
  read answer
  case "$answer" in
    [Yy]* | "" )
      INSTALL_LOCAL=true
      break
      ;;
    [Nn]* )
      INSTALL_LOCAL=false
      break
      ;;
    * )
      echo "Valid answers: y / n"
      ;;
  esac
done

# Set installation directory
JUNO_DIR=""
if [ "$INSTALL_LOCAL" = true ]; then
  JUNO_DIR="$HOME/.local/bin"
else
  JUNO_DIR="/usr/local/bin"
fi

# Final review
echo ""
echo "***Final review of options***"
echo ""
echo "Installation directory: $JUNO_DIR"
echo ""

# Confirm installation
while true; do
  echo "Do you wish to proceed? [Y/n]"
  read answer
  case "$answer" in
    [Yy]* | "" )
      break
      ;;
    [Nn]* )
      echo "Aborting installation. No files installed."
      exit 0
      ;;
    * )
      echo "Valid answers: y / n"
      ;;
  esac
done

echo ""
echo "Initiating installation..."

# Check if we need sudo for non-local install
SUDO=""
if [ "$INSTALL_LOCAL" = false ]; then
  SUDO="sudo"
fi

# Detect OS
OS=$(uname -s | tr '[:upper:]' '[:lower:]')

# Download latest release
echo "Downloading Juno from GitHub..."
RELEASE_URL="https://github.com/$REPO/releases/latest/download/juno-${OS}.tar.gz"

curl -fsSL "$RELEASE_URL" -o /tmp/juno.tar.gz || {
  echo "Error: Failed to download Juno from $RELEASE_URL"
  echo "Please check that a release exists for your platform."
  exit 1
}

# Create installation directory
$SUDO mkdir -p "$JUNO_DIR"

# Extract files
echo "Extracting files..."
cd /tmp
tar -xzf juno.tar.gz || {
  echo "Error: Failed to extract archive"
  rm /tmp/juno.tar.gz
  exit 1
}

# Install files
echo "Installing Juno..."
$SUDO cp -r juno/* "$JUNO_DIR/"
$SUDO chmod +x "$JUNO_DIR/juno"
if [ -f "$JUNO_DIR/junoc" ]; then
  $SUDO chmod +x "$JUNO_DIR/junoc"
fi

# Cleanup
rm -rf /tmp/juno /tmp/juno.tar.gz

echo ""
echo "Installation complete!"
echo ""
echo "Juno has been installed to: $JUNO_DIR/juno"

# Check if directory is in PATH
if [[ ":$PATH:" != *":$JUNO_DIR:"* ]]; then
  echo ""
  echo "WARNING: $JUNO_DIR is not in your PATH."
  echo "Add this line to your ~/.bashrc or ~/.zshrc:"
  echo "  export PATH=\"\$PATH:$JUNO_DIR\""
fi

echo ""
echo "Try running: juno help"
