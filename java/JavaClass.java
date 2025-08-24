public class JavaClass {
    static {
        System.loadLibrary("arrow_ffi_poc");
    }

    private static native int rust_implementation(int arg1);
    
    public static void main(String[] args) {
        System.out.println("Calling Rust native method...");
        int result = rust_implementation(42);
        System.out.println("Result from Rust: " + result);
    }
}
