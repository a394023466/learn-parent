package asm.test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.UnsupportedEncodingException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLDecoder;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM4;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 11:07
 */
public class ASMAgent {
    public static void premain(String agentArgs, Instrumentation inst) {

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className != null && className.equals("java/net/Inet4Address")) {
                    ClassReader classReader = new ClassReader(classfileBuffer);

                    ClassWriter classWriter = new ClassWriter(classReader, 0);


                    ClassVisitor changeClassVisitor = new ChangeClassVisitor(ASM4,classWriter);

                    classReader.accept(changeClassVisitor, 0);
                    return classWriter.toByteArray();
                }
                return classfileBuffer;
            }
        });

    }

    /**
     * 获取当前所在jar包的路径
     *
     * @return jar包路径
     */
    public static String getLocalJarPath() {
        URL localUrl = ASMAgent.class.getProtectionDomain().getCodeSource().getLocation();
        String path = null;
        try {
            path = URLDecoder.decode(
                    localUrl.getFile().replace("+", "%2B"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("[OpenRASP] Failed to get jarFile path.");
            e.printStackTrace();
        }
        return path;
    }
}
