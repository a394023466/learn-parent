package asm.generate;


import asm.MyClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 利用ASM生成类
 *
 * @author: liyuzhi
 * @date: 2020/11/11 19:05
 * @version: 1
 */

public class Test {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, FileNotFoundException {

        ClassWriter classWriter = new ClassWriter(0);
        PrintWriter printWriter=new PrintWriter("C:\\Users\\Lenovo\\Desktop\\a.txt");
        //使用PrintWriter这个类来输出修改类的信息到文件中
        TraceClassVisitor cv=new TraceClassVisitor(classWriter,printWriter);
        /**
         *
         *public class GenerateClass1 implements Serializable {
         *     public void print() {
         *         System.out.println("my name is GenerateClass1");
         *     }
         * }
         *
         */
        cv.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC,
                "asm/generate/GenerateClass",
                null,
                "java/lang/Object",
                new String[]{"java/io/Serializable"}
        );

        //生成构造函数
        final MethodVisitor methodVisitor = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
        );

        //调用visitVarInsn方法，生成aload指令， 将第0个本地变量（也就是this）压入操作数栈。
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        //调用visitMethodInsn方法， 生成invokespecial指令， 调用父类（也就是Object）的构造方法。
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        //调用visitInsn方法，生成return指令， 方法返回。
        methodVisitor.visitInsn(Opcodes.RETURN);
        //调用visitMaxs方法， 指定当前要生成的方法的最大局部变量和最大操作数栈。 对应Code属性中的max_stack和max_locals 。
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();


        //最后调用visitEnd方法， 表示当前要生成的构造方法已经创建完成。
        final MethodVisitor printMethodVisitor = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "print",
                "()V",
                null,
                null
        );
        //GETSTATIC：获取静态的java/lang/System.out压入操作数栈
        printMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        //  LDC ：将常量压入操作数栈中
        printMethodVisitor.visitLdcInsn("my name is GenerateClass1");
        //INVOKEVIRTUAL：调用java/io/PrintStream下的println方法，
        printMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        printMethodVisitor.visitInsn(Opcodes.RETURN);
        printMethodVisitor.visitMaxs(2, 1);
        printMethodVisitor.visitEnd();


        cv.visitEnd();
        final byte[] bytes = classWriter.toByteArray();


        MyClassLoader classLoader = new MyClassLoader();
        Class aClass = classLoader.defineClass("asm.generate.GenerateClass", bytes);

        final Method method = aClass.getMethod("print");
        method.invoke(aClass.newInstance());


    }
}
