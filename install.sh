#!/usr/bin/env bash

set -e

VERSION="0.3.3-alpha"
REPO="LoPalma/juno-lang"
ARCHIVE="juno-$VERSION-linux.tar.gz"
DOWNLOAD_URL="https://github.com/$REPO/releases/download/v$VERSION/$ARCHIVE"

echo "=== Juno Installer ==="
echo "Version: $VERSION"
echo ""

# Prompt helper
prompt() {
    local message=$1
    local default=$2
    read -p "$message [$default] " response
    response="${response:-$default}"
    echo "$response"
}

# Ask installation type
INSTALL_TYPE=$(prompt "Install system-wide or locally? (system/local)" "local")

if [[ "$INSTALL_TYPE" == "system" ]]; then
    INSTALL_DIR="/opt/juno"
else
    INSTALL_DIR="$HOME/.local/juno"
fi

# Ask about symlinks
CREATE_SYMLINKS=$(prompt "Create symlinks to /usr/local/bin? (Y/n)" "Y")

# Confirm
echo ""
echo "Installation summary:"
echo "  Version:     $VERSION"
echo "  Install dir: $INSTALL_DIR"
echo "  Symlinks:    $CREATE_SYMLINKS"
echo ""
read -p "Proceed with installation? [Y/n] " CONFIRM
CONFIRM="${CONFIRM:-Y}"

if [[ ! "$CONFIRM" =~ ^[Yy]$ ]]; then
    echo "Installation cancelled."
    exit 0
fi

echo ""
echo "Downloading release..."
TMP_DIR=$(mktemp -d)
cd "$TMP_DIR"
curl -LO "$DOWNLOAD_URL"

echo "Extracting archive..."
tar -xzf "$ARCHIVE"

cd "juno-$VERSION"

echo "Installing to $INSTALL_DIR..."
mkdir -p "$INSTALL_DIR"
cp -r . "$INSTALL_DIR"

if [[ "$CREATE_SYMLINKS" =~ ^[Yy]$ ]]; then
    echo "Creating symlinks in /usr/local/bin..."
    sudo ln -sf "$INSTALL_DIR/junoc" /usr/local/bin/junoc
    sudo ln -sf "$INSTALL_DIR/jpm" /usr/local/bin/jpm
fi

echo ""
echo "Cleaning up..."
cd ~
rm -rf "$TMP_DIR"

echo ""
echo "✅ Installation complete!"
echo "To uninstall, remove: $INSTALL_DIR"
if [[ "$CREATE_SYMLINKS" =~ ^[Yy]$ ]]; then
    echo "And delete symlinks in /usr/local/bin (junoc, jpm)"
fi
echo ""
echo "Try running:"
echo "  junoc examples/hello.juno"
echo ""
