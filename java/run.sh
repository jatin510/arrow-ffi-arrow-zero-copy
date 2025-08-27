#!/bin/bash

# Build the project first
mvn clean compile

# Set the library path and run the application
ARROW_LIB_PATH="$(pwd)/../arrow_ffi_poc/target/release"
CLASSPATH="target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)"

echo "Running Arrow FFI Demo..."
echo "Library path: $ARROW_LIB_PATH"
echo ""

java -cp "$CLASSPATH" \
     -Djava.library.path="$ARROW_LIB_PATH" \
     --add-opens=java.base/java.nio=ALL-UNNAMED \
     --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
     JavaClass
