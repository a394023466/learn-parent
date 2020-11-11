import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/6 11:35
 */
public class ChangeClassVisitor extends ClassAdapter {

    public ChangeClassVisitor(ClassVisitor classVisitor) {
        super(classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int i, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(i, name, desc, signature, exceptions);
        if (name.equals("getHostAddress")) {
            methodVisitor = new ChangClassVisitorHook(methodVisitor, i, name, desc);
        }
        return methodVisitor;
    }
}
