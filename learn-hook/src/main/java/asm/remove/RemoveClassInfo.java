package asm.remove;

import org.objectweb.asm.*;

/**
 * ClassVisitor方法的调用顺序为
 * <p>
 * visit
 * visitSource
 * visitField
 * visitMethod
 * visitMethod
 * visitEnd
 *
 * @author: liyuzhi
 * @date: 2020/11/12 15:28
 * @version: 1
 */
public class RemoveClassInfo extends ClassVisitor {

    private String methodName;
    private String methodDesc;

    public RemoveClassInfo(ClassVisitor classVisitor, String methodName, String methodDesc) {
        super(Opcodes.ASM4, classVisitor);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        //利用ASM框架移除指定的方法，实现方式就返回null
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            return null;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        //利用ASM框架移除指定的字段，实现方式就返回null
        if (name.equals("str") && descriptor.equals(Type.getType(String.class).getDescriptor())) {
            return null;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }
}
