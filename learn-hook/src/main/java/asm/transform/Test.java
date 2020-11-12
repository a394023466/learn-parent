package asm.transform;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 19:12
 */
public class Test {

    public static void premain1(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                if ("asm/ClassTest".equals(className)) {
                    System.out.println(className);
                    ClassReader  reader = new ClassReader(classfileBuffer);
                    ClassWriter writer = new ClassWriter(reader, 0);
                    TransformClass transformClass = new TransformClass(Opcodes.ASM4, writer);
                    reader.accept(transformClass, 0);
                    return writer.toByteArray();
                }
                return classfileBuffer;
            }
        });
    }
}
