package learn.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @version 读写锁测试
 * @author: Lenovo
 * @date:Create：in 2020/4/27 15:35
 */
public class ReadWriteLockTest {

    private int state;

    ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    public static void main(String[] args) {

        ReadWriteLockTest readWriteLockTest = new ReadWriteLockTest();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                readWriteLockTest.read();
                readWriteLockTest.write();
            }).start();
        }
    }


    public int read() {
        reentrantReadWriteLock.readLock().lock();

        try {
            System.out.println(Thread.currentThread().getName() + "获取到了读锁,读取到的state值为：" + state);
            return state;
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    public void write() {
        reentrantReadWriteLock.writeLock().lock();
        try {
            state++;
            System.out.println(Thread.currentThread().getName() + "获取到了写锁，将state值加到了：" + state);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }
}
