package asm.remove;

import asm.test.TestClass;
import asm.transform.TransformClass;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/12 14:56
 */
public class Test {

    public static void premain1(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                if ("asm/test/TestClass".equals(className)) {
                    System.out.println(className);
                    org.objectweb.asm.ClassReader reader = new org.objectweb.asm.ClassReader(classfileBuffer);
                    ClassWriter writer = new ClassWriter(reader, 0);
                    RemoveClassInfo removeClassInfo = new RemoveClassInfo(writer,"print","()V");
                    reader.accept(removeClassInfo, 0);
                    return writer.toByteArray();
                }
                return classfileBuffer;
            }
        });
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println(Type.getType(String.class).getDescriptor());
        System.out.println(Type.getType(String.class).getInternalName());

         PrintWriter printWriter=new PrintWriter("C:\\Users\\Lenovo\\Desktop\\a.txt");
        printWriter.write("aaaa");
        printWriter.close();
    }
}
