package asm.test;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 11:11
 */
public class ChangClassVisitorHook extends AdviceAdapter {

    private String desc;

    protected ChangClassVisitorHook(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
        this.desc=descriptor;
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (opcode != 191 && "()Ljava/lang/String;".equals(desc)) {
            invokeStatic(Type.getType(ChangClassVisitorHook.class), new Method("onInputStreamRead", "()V"));
        }

    }

    /**
     * hook函数
     */
    public static void onInputStreamRead() {
        System.out.println("onInputStreamRead====");
    }
}
