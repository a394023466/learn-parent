package asm.transform;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 19:11
 */
public class TransformClass extends ClassVisitor {

    public TransformClass(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    /**
     * 访问转换类的类信息
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(Opcodes.V1_7, access, name, signature, superName, interfaces);
    }

    /**
     * 访问转换类的方法信息
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("-------------------");
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("print")) {
            methodVisitor = new TransformMethod(methodVisitor);
        }
        return methodVisitor;
    }
}
