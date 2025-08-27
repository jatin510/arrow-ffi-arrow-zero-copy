use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::jint;

#[unsafe(no_mangle)]
pub extern "system" fn Java_JavaClass_rust_1implementation(
    env: JNIEnv,
    _class: JClass,
    arg1: jint,
) -> jint {
    println!("Hello, world! from rust");
    println!("arg1: {}", arg1);
    arg1 + 1
}
