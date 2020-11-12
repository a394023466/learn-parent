package asm;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 18:35
 */
public class MyClassLoader extends ClassLoader {
    public Class defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
