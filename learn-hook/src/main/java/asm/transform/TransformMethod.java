package asm.transform;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 19:46
 */
public class TransformMethod extends MethodVisitor {


    public TransformMethod(  MethodVisitor methodVisitor) {
        super(Opcodes.ASM4, methodVisitor);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode==Opcodes.RETURN){
            //GETSTATIC：获取静态的java/lang/System.out压入操作数栈
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            //  LDC ：将常量压入操作数栈中
            mv.visitLdcInsn("my name is GenerateClass1");
            //INVOKEVIRTUAL：调用java/io/PrintStream下的println方法，
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }


}
