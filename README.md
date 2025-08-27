# Arrow FFI Proof of Concept

This project demonstrates Java Native Interface (JNI) integration between Java and Rust with Apache Arrow data structures. The Java application creates Arrow batches of user data and calls native Rust functions through FFI for zero-copy data exchange.

## Project Structure

```
arrow-ffi/
├── arrow_ffi_poc/          # Rust library crate
│   ├── Cargo.toml          # Rust project configuration
│   └── src/
│       ├── lib.rs          # Library entry point
│       └── native_implemenation_arrow.rs  # JNI implementation
├── java/                   # Java application (Maven project)
│   ├── pom.xml             # Maven configuration with Arrow dependencies
│   ├── run.sh              # Shell script to run the application
│   └── src/main/java/
│       └── JavaClass.java  # Java class with Arrow batch creation and native calls
├── .gitignore              # Git ignore patterns for build artifacts
└── README.md               # This file
```

## Prerequisites

- **Rust**: Install from [rustup.rs](https://rustup.rs/)
- **Java JDK**: Java 11 or higher (required for Apache Arrow)
- **Maven**: For Java dependency management
- **Operating System**: macOS, Linux, or Windows

## How to Build and Run

### Step 1: Build the Rust Native Library

Navigate to the Rust project directory and build the library:

```bash
cd arrow_ffi_poc
cargo build --release
```

This creates the native library at `target/release/libarrow_ffi_poc.dylib` (macOS) or equivalent for your platform.

### Step 2: Build and Run the Java Application

Navigate to the Java directory and use the run script:

```bash
cd java
./run.sh
```

Or manually with Maven:

```bash
cd java
mvn clean compile
java -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
     -Djava.library.path="../arrow_ffi_poc/target/release" \
     --add-opens=java.base/java.nio=ALL-UNNAMED \
     --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
     JavaClass
```

### Expected Output

```
=== Arrow FFI Demo ===

1. Testing original Rust function:
Hello, world!
arg1: 42
   Result from Rust: 43

2. Creating Arrow batch with user data:
   Schema: Schema<id: Int(32, true), name: Utf8, age: Int(32, true), email: Utf8, salary: Utf8>
   Created batch with 5 rows
   User Data:
     Row 0: ID=1, Name='Alice Johnson', Age=28, Email='alice@example.com', Salary='$75,000'
     Row 1: ID=2, Name='Bob Smith', Age=34, Email='bob@company.org', Salary='$85,000'
     Row 2: ID=3, Name='Carol Williams', Age=31, Email='carol@tech.io', Salary='$92,000'
     Row 3: ID=4, Name='David Brown', Age=45, Email='david@startup.net', Salary='$110,000'
     Row 4: ID=5, Name='Eva Davis', Age=29, Email='eva@corp.com', Salary='$68,000'
   Serialized batch size: 1096 bytes
   ✓ Arrow batch created successfully!
```

## Technical Details

### Rust Implementation

- **Crate Type**: `cdylib` - Creates a C-compatible dynamic library
- **JNI Binding**: Uses the `jni` crate for Java integration
- **Function Signature**: Follows JNI naming convention `Java_ClassName_methodName`

### Java Implementation

- **Apache Arrow**: Creates columnar data batches for efficient processing
- **User Data Schema**: ID (Int32), Name (Utf8), Age (Int32), Email (Utf8), Salary (Utf8)
- **Native Method**: `rust_implementation(int arg1)` declared as `native`
- **Library Loading**: `System.loadLibrary("arrow_ffi_poc")` loads the Rust library
- **Memory Management**: Uses Arrow's RootAllocator for off-heap memory
- **Serialization**: Converts Arrow batches to bytes for potential FFI transfer

### Key Files

1. **`arrow_ffi_poc/Cargo.toml`**: Rust project configuration with JNI dependency
2. **`arrow_ffi_poc/src/native_implemenation_arrow.rs`**: Contains the JNI function implementation
3. **`java/pom.xml`**: Maven configuration with Apache Arrow dependencies
4. **`java/src/main/java/JavaClass.java`**: Java class with Arrow batch creation and native calls
5. **`java/run.sh`**: Convenient script to build and run the application

## Development Workflow

1. **Modify Rust Code**: Edit functions in `native_implemenation_arrow.rs`
2. **Rebuild Library**: Run `cargo build --release` in `arrow_ffi_poc/`
3. **Modify Java Code**: Edit `java/src/main/java/JavaClass.java` for Arrow batch changes
4. **Test**: Run `./run.sh` in `java/` directory to build and test changes

## Arrow Features Demonstrated

- **Schema Definition**: Structured data with multiple column types
- **Vector Creation**: Efficient columnar storage with IntVector and VarCharVector
- **Memory Management**: Proper allocation and cleanup with try-with-resources
- **Data Population**: Sample user data with 5 records
- **Serialization**: Converting Arrow batches to byte arrays for FFI transfer
- **Zero-Copy Potential**: Foundation for efficient data exchange between Java and Rust

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
