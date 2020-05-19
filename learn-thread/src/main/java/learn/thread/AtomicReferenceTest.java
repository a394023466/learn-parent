package learn.thread;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * @author: Lenovo
 * @date:Create：in 2020/4/28 9:30
 */
public class AtomicReferenceTest {
    public static void main(String[] args) {
        final Thread initThread = new Thread();
        System.out.println("初始化线程的名字为：" + initThread.getName());
        //初始化AtomicReference时候存入一个值
        AtomicReference<Thread> atomicReference = new AtomicReference<>(initThread);

        final Thread thread = atomicReference.updateAndGet(new UnaryOperator<Thread>() {
            @Override
            public Thread apply(Thread thread) {
                thread.setName("更新了线程的名字");
                return thread;
            }
        });
        System.out.println(thread);
        System.out.println(atomicReference.get());
    }

    private static void getAndAccumulate() {
        final Thread initThread = new Thread();
        System.out.println("初始化AtomicReference内部的值为：" + initThread);
        //初始化AtomicReference时候存入一个值
        AtomicReference<Thread> atomicReference = new AtomicReference<>(initThread);
        //要传入的新值
        final Thread initThread1 = new Thread();

        /**
         * 该方法返回值一直为AtomicReference内部的旧值
         */
        final Thread andAccumulate = atomicReference.getAndAccumulate(initThread1, new BinaryOperator<Thread>() {
            /**
             * 根据返回值来保留AtomicReference内部的值，但是返回值不影响getAndAccumulate方法的返回值，getAndAccumulate一直返回旧值
             * @param thread AtomicReference内部的旧值
             * @param thread2 传入的新值
             * @return 返回那个值，如果返回新值，AtomicReference内部则为新值，如果返回旧值，AtomicReference内部则为旧值。
             */
            @Override
            public Thread apply(Thread thread, Thread thread2) {
                System.out.println("AtomicReference内部的旧值为：" + thread);
                System.out.println("传入的新值为：" + thread2);
                //如果返回新值，AtomicReference内部则更新成新值，如果返回旧值AtomicReference内部更新为旧值。
                return thread2;
            }
        });

        System.out.println("getAndAccumulate返回的值为：" + andAccumulate);
        System.out.println("AtomicReference内部的值为：" + atomicReference.get());
    }

    private static void accumulateAndGet() {
        final Thread initThread = new Thread();
        System.out.println("初始化AtomicReference内部的值为：" + initThread);

        //初始化AtomicReference时候存入一个值
        AtomicReference<Thread> atomicReference = new AtomicReference<>(initThread);
        //要传入的新值
        final Thread newThread = new Thread();
        System.out.println("初始化新值为：" + initThread);
        final Thread thread = atomicReference.accumulateAndGet(newThread, new BinaryOperator<Thread>() {

            /**
             *  根据返回值来保留AtomicReference内部的值
             * @param initThread  AtomicReference内部的值
             * @param thread2 用户指定的新值
             * @return 返回那个值，如果返回新值，AtomicReference内部则为新值，如果返回旧值，AtomicReference内部则为旧值。
             */
            @Override
            public Thread apply(Thread initThread, Thread thread2) {
                System.out.println("AtomicReference内部的旧值为：" + initThread);
                System.out.println("传入的新值为：" + thread2);
                //如果返回新值，AtomicReference内部则为新值，如果返回旧值，AtomicReference内部则为旧值。
                return initThread;
            }
        });
        System.out.println("返回AtomicReference内部的值为：" + thread);
        System.out.println("返回AtomicReference内部的值为：" + atomicReference.get());
    }

    private static void getAndSet() {
        AtomicReference<Thread> atomicReference = new AtomicReference<>();

        final Thread thread1 = new Thread();
        final Thread thread2 = new Thread();
        System.out.println(thread1);
        System.out.println(thread2);
        /**
         * AtomicReference初始化的时候内部对象引用为null，调用compareAndSet(null, thread1)的时候
         * 由于AtomicReference内部对象引用为null，然后传入的旧的对象引用为null，传入的新的引用为thread1。
         * 因为AtomicReference内部对象引用null等于传入对象引用null，所以当前方法执行成功。
         */
        atomicReference.compareAndSet(null, thread1);
        /**
         * 替换AtomicReference内部对象的引用，返回被替换的对象引用
         */
        final Thread oldThread = atomicReference.getAndSet(thread2);
        final Thread newThread = atomicReference.get();
        System.out.println(oldThread);
        System.out.println(newThread);
    }


    public void compareAndSet() {
        AtomicReference<Thread> atomicReference = new AtomicReference<>();

        final Thread thread1 = new Thread();
        final Thread thread2 = new Thread();
        /**
         * AtomicReference初始化的时候内部对象引用为null，调用compareAndSet(null, thread1)的时候
         * 由于AtomicReference内部对象引用为null，然后传入的旧的对象引用为null，传入的新的引用为thread1。
         * 因为AtomicReference内部对象引用null等于传入对象引用null，所以当前方法执行成功。
         */
        final boolean b = atomicReference.compareAndSet(null, thread1);

        /**
         * 上边的方法我们已经把AtomicReference内部对象的因为改为了thread1，当调用compareAndSet(thread2, thread1)的时候
         * 由于AtomicReference内部对象引用为thread1，然后传入的旧的对象引用为thread2，传入的新的引用为thread1。
         * 因为AtomicReference内部对象引用thread1不等于传入对象引用thread2，所以当前方法执行失败。
         */
        final boolean b1 = atomicReference.compareAndSet(thread2, thread1);
        System.out.println(b);
        System.out.println(b1);
    }
}
