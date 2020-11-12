package asm.analysis;


import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 16:30
 */
public class Test {
    public static void main(String[] args) throws IOException {
        /**
         * ClassReader是一个容器，该容器用于加载将要被修改或者转换的类
         */
        ClassReader classReader = new ClassReader("asm.test.TestClass");
        /**
         * 该类继承了ClassVisitor抽象类，功能是访问或者修改即将被修改的类中的信息，信息包含类的名字、方法、字段等详细信息
         */
        AnalysisClass visitorClassPrinter = new AnalysisClass();

        /**
         * 该方法就是将具体的修改或者转换逻辑作用在被 ClassReader加载进来的即将被修改的类
         */
        classReader.accept(visitorClassPrinter, 0);
    }
}
