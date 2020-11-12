package asm.add;

import asm.test.TestClass;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
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
                    AddClassInfo addClassInfo = new AddClassInfo(writer,"str1","Ljava/lang/String;");
                    reader.accept(addClassInfo, 0);
                    return writer.toByteArray();
                }
                return classfileBuffer;
            }
        });
    }

    public static void main(String[] args) throws NoSuchFieldException {
        TestClass testClass = new TestClass();
        testClass.print();
        Field str1 = testClass.getClass().getField("str1");
        System.out.println(str1.getName());

    }
}
