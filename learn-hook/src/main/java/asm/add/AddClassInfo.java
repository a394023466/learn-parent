package asm.add;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/12 15:47
 */
public class AddClassInfo extends ClassVisitor {
    private String fieldName;
    private String fieldDesc;

    private boolean isFieldPresent;


    public AddClassInfo(ClassVisitor classVisitor, String fieldName, String fieldDesc) {
        super(Opcodes.ASM4, classVisitor);
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        //判断当前的类中是否已经存在了该方法
        if (name.equals(fieldName)) {
            isFieldPresent = true;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        //如果不存在则向类中添加新字段
        if (!isFieldPresent) {
            //重新生成一个FileVisitor，
            FieldVisitor fieldVisitor = cv.visitField(Opcodes.ACC_PUBLIC, fieldName, fieldDesc, null, null);
            if (fieldVisitor != null) {
                fieldVisitor.visitEnd();
            }
        }
        cv.visitEnd();
    }
}
