package asm;


import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class TraceClasvisitorTest {
    /**
     * 该测试功能是将java.io.File类中的public String[] list()方法名改成list111111111
     * 并将修改后的类文件输出到指定文件中。
     */
    public static void main(String[] args) throws IOException {
        ClassReader reader = new ClassReader(Type.getInternalName(File.class));
        ClassWriter classWriter = new ClassWriter(reader, 0);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(classWriter, new PrintWriter("C:\\Users\\Lenovo\\Desktop\\a.txt"));

        reader.accept(new ClassVisitor(Opcodes.ASM4, traceClassVisitor) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (name.equals("list") && descriptor.equals("()[Ljava/lang/String;")) {
                    return super.visitMethod(access, "list111111111", descriptor, signature, exceptions);
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        }, 0);

        traceClassVisitor.visitEnd();

    }

}
