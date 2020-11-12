package asm.test;


import asm.test.ChangClassVisitorHook;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/6 11:35
 */
public class ChangeClassVisitor extends ClassVisitor {

    public ChangeClassVisitor() {
        super(ASM4);
    }

    public ChangeClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("getHostAddress")) {
            methodVisitor = new ChangClassVisitorHook(ASM4, methodVisitor, access, name, descriptor);
        }
        return methodVisitor;
    }
}
