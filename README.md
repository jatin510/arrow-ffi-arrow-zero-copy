# Arrow FFI Proof of Concept

This project demonstrates Java Native Interface (JNI) integration between Java and Rust, where Java calls native Rust functions through FFI.

## Project Structure

```
arrow-ffi/
├── arrow_ffi_poc/          # Rust library crate
│   ├── Cargo.toml          # Rust project configuration
│   └── src/
│       ├── lib.rs          # Library entry point
│       └── native_implemenation_arrow.rs  # JNI implementation
├── java/                   # Java application
│   └── JavaClass.java      # Java class with native method calls
├── .gitignore              # Git ignore patterns for build artifacts
└── README.md               # This file
```

## Prerequisites

- **Rust**: Install from [rustup.rs](https://rustup.rs/)
- **Java JDK**: Java 8 or higher
- **Operating System**: macOS, Linux, or Windows

## How to Build and Run

### Step 1: Build the Rust Native Library

Navigate to the Rust project directory and build the library:

```bash
cd arrow_ffi_poc
cargo build --release
```

This creates the native library at `target/release/libarrow_ffi_poc.dylib` (macOS) or equivalent for your platform.

### Step 2: Compile the Java Class

Navigate to the Java directory and compile:

```bash
cd java
javac JavaClass.java
```

### Step 3: Run the Java Application

Run the Java class with the native library path:

```bash
java -Djava.library.path=../arrow_ffi_poc/target/release JavaClass
```

### Expected Output

```
Calling Rust native method...
Hello, world!
Result from Rust: 43
```

## Technical Details

### Rust Implementation

- **Crate Type**: `cdylib` - Creates a C-compatible dynamic library
- **JNI Binding**: Uses the `jni` crate for Java integration
- **Function Signature**: Follows JNI naming convention `Java_ClassName_methodName`

### Java Implementation

- **Native Method**: `rust_implementation(int arg1)` declared as `native`
- **Library Loading**: `System.loadLibrary("arrow_ffi_poc")` loads the Rust library
- **JNI Integration**: Seamless calls between Java and Rust

### Key Files

1. **`arrow_ffi_poc/Cargo.toml`**: Rust project configuration with JNI dependency
2. **`arrow_ffi_poc/src/native_implemenation_arrow.rs`**: Contains the JNI function implementation
3. **`java/JavaClass.java`**: Java class that calls the native Rust function

## Development Workflow

1. **Modify Rust Code**: Edit functions in `native_implemenation_arrow.rs`
2. **Rebuild Library**: Run `cargo build --release` in `arrow_ffi_poc/`
3. **Recompile Java**: Run `javac JavaClass.java` in `java/` (if Java code changed)
4. **Test**: Run the Java application with the updated library

## Platform Notes

- **macOS**: Library extension is `.dylib`
- **Linux**: Library extension is `.so`
- **Windows**: Library extension is `.dll`

The build process automatically creates the correct library type for your platform.

## Git Configuration

The project includes a comprehensive `.gitignore` file that excludes:

- **Rust build artifacts**: `target/`, `*.rs.bk`, `Cargo.lock`
- **Java build artifacts**: `*.class`, `*.jar`, `*.war`
- **Native libraries**: `*.so`, `*.dylib`, `*.dll`
- **JNI generated files**: `java/out/`, `**/*.h`
- **IDE files**: `.vscode/`, `.idea/`, `*.iml`
- **OS specific files**: `.DS_Store`, `Thumbs.db`
- **Temporary and log files**: `*.log`, `*.tmp`, `*.bak`

## Troubleshooting

### Common Issues

1. **`UnsatisfiedLinkError`**: 
   - Ensure the Rust library is built (`cargo build --release`)
   - Verify the library path in the `java.library.path` argument

2. **`ClassNotFoundException`**:
   - Make sure you're running `java` from the directory containing `JavaClass.class`

3. **Build Errors**:
   - Ensure Rust and Java are properly installed
   - Check that all dependencies are available

### Debug Tips

- Use `ldd` (Linux) or `otool -L` (macOS) to inspect library dependencies
- Add print statements in both Java and Rust for debugging
- Check that function signatures match between Java declarations and Rust implementations
