package instrumentation;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 11:07
 */
public class InstrumentationAgent {


    public static void premain1(String agentArgs, Instrumentation inst) {
        ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className != null && className.equals("java/net/Inet4Address")) {
                    try {
                        CtClass ctClass = classPool.get("java.net.Inet4Address");
                        CtMethod declaredMethod = ctClass.getDeclaredMethod("getHostAddress");
                        declaredMethod.insertAfter("System.out.println(1111111111);\n");
                        return ctClass.toBytecode();
                    } catch (NotFoundException | CannotCompileException | IOException e) {
                        e.printStackTrace();
                    }

                }
                return classfileBuffer;
            }
        });

    }


}
