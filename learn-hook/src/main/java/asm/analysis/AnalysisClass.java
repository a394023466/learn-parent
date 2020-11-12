package asm.analysis;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.ASM4;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 16:25
 */
public class AnalysisClass extends ClassVisitor {

    public AnalysisClass() {
        super(ASM4);
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
       // System.out.println(name.concat(" extends ").concat(superName));
        System.out.println("visit");
    }

    @Override
    public void visitSource(String source, String debug) {
        System.out.println("visitSource");
        super.visitSource(source, debug);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        System.out.println("visitAnnotation");
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        System.out.println("visitAttribute");

        super.visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        System.out.println("visitInnerClass");

        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println("visitField");
//        System.out.println("字段名为：".concat(name).concat(" 字段类型为：").concat(desc));
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        System.out.println("方法名为：".concat(name).concat(" 方法类型和返回值为：").concat(desc));
        System.out.println("visitMethod");

        return null;

    }

    @Override
    public void visitEnd() {
        System.out.println("visitEnd");
        super.visitEnd();
    }
}
